package kda.learn.microservices.project.services.disease.repository;

import kda.learn.microservices.project.services.disease.model.Disease;

import java.util.List;

public interface DiseaseStorage {
    List<Disease> getDiseases();
}
