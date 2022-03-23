package kda.learn.microservices.project.services.drugs;

import kda.learn.microservices.project.services.drugs.model.DrugInfo;

import java.util.List;

public interface DrugsService {
    List<DrugInfo> drugsForDisease(String diseaseCode);

    List<DrugInfo> findDrugs(String drug);
}
