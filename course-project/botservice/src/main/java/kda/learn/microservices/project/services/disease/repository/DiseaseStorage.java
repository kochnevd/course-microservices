package kda.learn.microservices.project.services.disease.repository;

import kda.learn.microservices.project.services.disease.model.Disease;
import kda.learn.microservices.project.services.disease.model.TreatmentTips;

import java.util.List;

public interface DiseaseStorage {
    List<Disease> getDiseases();

    TreatmentTips findTreatmentTips(String diseaseCode);
}
