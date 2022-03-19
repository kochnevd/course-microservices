package kda.learn.microservices.project.services.disease.repository;

import kda.learn.microservices.project.services.CommonFileUtils;
import kda.learn.microservices.project.services.disease.model.Disease;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.InvalidPropertiesFormatException;
import java.util.List;

@Repository
public class DiseaseStorageResources implements DiseaseStorage {

    private final static List<Disease> DISEASE_LIST = loadDiseases();

    private static final String DISEASE_FILE = "data/entities.disease.csv";
    private static final String FIELDS_SEPARATOR = ";";

    @Override
    public List<Disease> getDiseases() {
        return DISEASE_LIST;
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
}
