package kda.learn.microservices.project.services.disease.repository;

import kda.learn.microservices.project.services.CommonFileUtils;
import kda.learn.microservices.project.services.disease.model.Disease;
import kda.learn.microservices.project.services.disease.model.TreatmentTips;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.InvalidPropertiesFormatException;
import java.util.List;
import java.util.StringJoiner;

@Repository
public class DiseaseStorageResources implements DiseaseStorage {

    private static final Logger log = LoggerFactory.getLogger(DiseaseStorageResources.class);

    private final static List<Disease> DISEASE_LIST = loadDiseases();
    private final static List<TreatmentTips> TREATMENT_TIPS_LIST = loadTreatmentTips();

    private static final String DISEASE_FILE = "data/entities.disease.csv";
    private static final String TREATMENT_TIPS_FILE = "data/entities.treatment-tips.inf";
    private static final String FIELDS_SEPARATOR = ";";
    private static final String TT_HEADER_PREFIX = "***";
    private static final String TT_URL_PREFIX = "===";

    @Override
    public List<Disease> getDiseases() {
        return DISEASE_LIST;
    }

    @Override
    public TreatmentTips findTreatmentTips(String diseaseCode) {
        return TREATMENT_TIPS_LIST
                .stream()
                .filter(treatmentTips -> treatmentTips.getDiseaseCode().equals(diseaseCode))
                .findAny()
                .orElse(null);
    }

    private static List<Disease> loadDiseases() {
        try {
            String data = CommonFileUtils.loadDataFile(DISEASE_FILE);

            List<Disease> res = new ArrayList<>();

            for (var line : data.split("[\n\r]+")) {
                var fields = line.split(FIELDS_SEPARATOR);
                try {
                    var item = new Disease()
                            .code(fields[0])
                            .name(fields[1])
                            .symptoms(new ArrayList<>(Arrays.asList(fields).subList(2, fields.length)));
                    res.add(item);
                } catch (ArrayIndexOutOfBoundsException e) {
                    throw new InvalidPropertiesFormatException(String.format("Ошибка обработки строки <%s>", line));
                }
            }

            return res;

        } catch (IOException e) {
            throw new RuntimeException("Ошибка загрузки данных.", e);
        }
    }

    private static List<TreatmentTips> loadTreatmentTips() {
        try {
            String[] dataLines = CommonFileUtils.loadDataFile(TREATMENT_TIPS_FILE).split("[\n\r]+");

            List<TreatmentTips> res = new ArrayList<>();

            var lineNum = 0;
            while (lineNum < dataLines.length) {
                // Header
                var line = dataLines[lineNum++];
                if (!line.startsWith(TT_HEADER_PREFIX)) continue;
                var diseaseCode = line.substring(TT_HEADER_PREFIX.length());

                // Text
                var text = new StringJoiner("\n");
                do {
                    if (lineNum >= dataLines.length) break;
                    line = dataLines[lineNum++];
                    if (line.startsWith(TT_HEADER_PREFIX) || line.startsWith(TT_URL_PREFIX)) break;
                    text.add(line);
                } while (true);

                // Create new TreatmentTips
                var treatmentTips = new TreatmentTips(diseaseCode, text.toString());
                res.add(treatmentTips);

                // URLs
                while (line.startsWith(TT_URL_PREFIX)) {
                    var parts = line.split(TT_URL_PREFIX);
                    if (parts.length != 3)
                        log.warn("Некорректный формат строки URL для подсказок ({})", line);
                    else
                        treatmentTips.addUrl(parts[1], parts[2]);

                    if (lineNum >= dataLines.length) break;
                    line = dataLines[lineNum++];
                }
                lineNum--; // Последняя строчка не была URL, вернемся на нее для следующей итерации
            }

            return res;

        } catch (IOException e) {
            throw new RuntimeException("Ошибка загрузки данных.", e);
        }
    }
}
