package kda.learn.microservices.project.services.disease;

import kda.learn.microservices.project.services.disease.model.Disease;
import kda.learn.microservices.project.services.nlp.NlpSymptoms;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DiseaseService {

    private final NlpSymptoms nlpSymptoms;

    public DiseaseService(NlpSymptoms nlpSymptoms) {
        this.nlpSymptoms = nlpSymptoms;
    }

    public List<Disease> guessDisease(String text) {
        return nlpSymptoms
                .guessDisease(text)
                .stream()
                .map(s -> new Disease().code(s).name(s))
                .collect(Collectors.toList());
    }
}
