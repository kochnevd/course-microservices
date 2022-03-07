package kda.learn.microservices.project.services.disease;

import kda.learn.microservices.project.services.disease.model.Disease;
import kda.learn.microservices.project.services.nlp.NlpSymptoms;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DiseaseService {

    private final NlpSymptoms nlpSymptoms;

    private final List<Disease> DISEASES = loadDiseases();
    private final Disease UNKNOWN_DISEASE = new Disease().code("unknown").name("Неопознанная болезнь");

    public DiseaseService(NlpSymptoms nlpSymptoms) {
        this.nlpSymptoms = nlpSymptoms;
    }

    public List<Disease> guessDisease(String text) {
        return nlpSymptoms
                .guessDisease(text)
                .stream()
                .map(this::findDisease)
                .collect(Collectors.toList());
    }

    private Disease findDisease(String code) {
        return DISEASES
                .stream()
                .filter(disease -> disease.getCode().equalsIgnoreCase(code))
                .findAny()
                .orElse(UNKNOWN_DISEASE);
    }

    private List<Disease> loadDiseases() {
        var res = new ArrayList<Disease>();

        res.add(new Disease().code("headache").name("Мигрень").symptoms(List.of("Головная боль", "Тошнота")));
        res.add(new Disease().code("toothache").name("Кариес").symptoms(List.of("Зубная боль")));

        return res;
    }
}
