package kda.learn.microservices.project.services.disease;

import kda.learn.microservices.project.services.disease.model.Disease;
import kda.learn.microservices.project.services.disease.model.TreatmentTips;

import java.util.List;

public interface DiseaseService {
    List<Disease> guessDisease(String text);
    TreatmentTips findTreatmentTips(String diseaseCode);
}
