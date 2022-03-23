package kda.learn.microservices.project.services.drugs;

import kda.learn.microservices.project.services.drugs.model.DrugInfo;
import kda.learn.microservices.project.services.drugs.repository.DrugsStorage;
import kda.learn.microservices.project.services.drugs.repository.entities.DrugEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DrugsServiceImpl implements DrugsService {
    private final DrugsStorage drugsStorage;

    public DrugsServiceImpl(DrugsStorage drugsStorage) {
        this.drugsStorage = drugsStorage;
    }

    @Override
    public List<DrugInfo> drugsForDisease(String diseaseCode) {
        List<DrugEntity> drugEntities = drugsStorage.getDrugsByDisease(diseaseCode);
        return drugEntities
                .stream()
                .map(this::transform)
                .collect(Collectors.toList());
    }

    @Override
    public List<DrugInfo> findDrugs(String drug) {
        return drugsStorage.getDrugsByName(drug)
                .stream()
                .map(this::transform)
                .collect(Collectors.toList());
    }

    private DrugInfo transform(DrugEntity drugEntity) {
        return new DrugInfo()
                .atx(drugEntity.getAtxCode())
                .name(drugEntity.getName())
                .instructions(drugEntity.getInstructions());
    }
}
