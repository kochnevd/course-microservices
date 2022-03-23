package kda.learn.microservices.project.services.drugs.repository;

import kda.learn.microservices.project.services.drugs.repository.entities.DrugEntity;

import java.util.List;

public interface DrugsStorage {
    List<DrugEntity> getDrugsByDisease(String diseaseCode);
    List<DrugEntity> getDrugsByName(String drugName);
}
