package kda.learn.microservices.project.services.disease;

import kda.learn.microservices.project.services.disease.model.Disease;
import kda.learn.microservices.project.services.disease.model.TreatmentTips;
import kda.learn.microservices.project.services.nlp.NlpSymptomsService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DiseaseServiceImpl implements DiseaseService {

    private final NlpSymptomsService nlpSymptoms;

    private final List<Disease> DISEASES = loadDiseases();
    private final Disease UNKNOWN_DISEASE = new Disease().code("unknown").name("Неопознанная болезнь");

    public DiseaseServiceImpl(NlpSymptomsService nlpSymptoms) {
        this.nlpSymptoms = nlpSymptoms;
    }

    @Override
    public List<Disease> guessDisease(String text) {
        return nlpSymptoms
                .guessDisease(text)
                .stream()
                .map(this::findDisease)
                .collect(Collectors.toList());
    }

    @Override
    public TreatmentTips findTreatmentTips(String diseaseCode) {
        if (diseaseCode.equals("headache")) {
            var res = new TreatmentTips(
                    "<b>Мигрень</b>\n" +
                            "Лечение мигрени делится на два направления:\n" +
                    "- облегчение состояния во время приступа;\n" +
                    "- предупреждение новых приступов\n\n" +
                    "Помощь при приступе:\n" +
                            "- снижение шума, свежий воздух, постель\n" +
                            "- обезболивающие препараты\n" +
                            "- при необходимости противорвотные средства\n\n" +
                    "Профилактика между приступами:\n" +
                            "- антидепрессанты\n" +
                            "- противосудорожные препараты\n" +
                            "бета-адреноблокаторы (пропранолол и средства на его основе)");
            res.addUrl("https://ru.wikipedia.org/wiki/Мигрень", "Мигрень - Википедия");
            res.addUrl("https://7010303.ru/zabolevanija/migren/", "Мигрень: симптомы, причины, лечение");
            return res;
        }
        else
            return null;
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
