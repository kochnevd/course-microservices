package kda.learn.microservices.project.services.drugs.repository;

import kda.learn.microservices.project.services.CommonFileUtils;
import kda.learn.microservices.project.services.drugs.repository.entities.DiseaseAtxEntity;
import kda.learn.microservices.project.services.drugs.repository.entities.DrugEntity;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.InvalidPropertiesFormatException;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class DrugsStorageResources implements DrugsStorage {

    private final static List<DrugEntity> DRUG_ENTITIES = loadDrugEntities();
    private final static List<DiseaseAtxEntity> DISEASE_ATX_ENTITIES = loadDiseaseAtxEntities();
    private static final String DRUG_ENTITIES_FILE = "data/entities.drug.csv";
    private static final String DISEASE_ATX_ENTITIES_FILE = "data/entities.disease-atx.csv";
    private static final String FIELDS_SEPARATOR = ";";
    public static final String ATX_SEPARATOR = ",";

    private static List<DrugEntity> loadDrugEntities() {

        try {
            String data = CommonFileUtils.loadDataFile(DRUG_ENTITIES_FILE);

            List<DrugEntity> res = new ArrayList<>();

            for (var line : data.split("[\n\r]+")) {
                var fields = line.split(FIELDS_SEPARATOR);
                try {
                    var item = new DrugEntity();
                    item.setName(fields[0]);
                    item.setAtxCode(fields[1]);
                    item.setInstructions(fields.length > 2 ? fields[2] : "");
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

    private static List<DiseaseAtxEntity> loadDiseaseAtxEntities() {

        try {
            String data = CommonFileUtils.loadDataFile(DISEASE_ATX_ENTITIES_FILE);

            List<DiseaseAtxEntity> res = new ArrayList<>();

            for (var line : data.split("[\n\r]+")) {
                var fields = line.split(FIELDS_SEPARATOR);
                var item = new DiseaseAtxEntity();
                item.setDiseaseCode(fields[0]);
                item.setAtxCodes(Arrays.stream(fields[1].split(ATX_SEPARATOR)).collect(Collectors.toSet()));
                res.add(item);
            }

            return res;

        } catch (IOException e) {
            throw new RuntimeException("Ошибка загрузки данных.", e);
        }

    }

    @Override
    public List<DrugEntity> getDrugsByDisease(String diseaseCode) {
        var filterByAtx = DRUG_ENTITIES.stream()
                .filter(drugEntity -> {
                    for (DiseaseAtxEntity diseaseAtxEntity : DISEASE_ATX_ENTITIES) {
                        if (diseaseAtxEntity.getDiseaseCode().equalsIgnoreCase(diseaseCode)) {
                            if (diseaseAtxEntity.getAtxCodes().contains(drugEntity.getAtxCode())) {
                                return true;
                            }
                        }
                    }
                    return false;
                });

        return filterByAtx.collect(Collectors.toList());
    }

    @Override
    public List<DrugEntity> getDrugsByName(String drugName) {
        var regexp = drugName.replace("*", ".*").toLowerCase();
        return DRUG_ENTITIES.stream()
                .filter(drugEntity -> drugEntity.getName().toLowerCase().matches(regexp))
                .collect(Collectors.toList());
    }
}
