package kda.learn.microservices.project.cure;

import kda.learn.microservices.project.cure.model.UserInfo;
import kda.learn.microservices.project.services.disease.DiseaseService;
import org.springframework.stereotype.Component;

@Component
public class UserMessageProcessor {

    private final DiseaseService diseaseService;

    public UserMessageProcessor(DiseaseService diseaseService) {
        this.diseaseService = diseaseService;
    }

    public String processMessage(String text, UserInfo userInfo) {
        return diseaseService
                .guessDisease(text)
                .stream()
                .map(disease -> disease.getCode() + ": " + disease.getName())
                .reduce((s, s2) -> s + "\n" + s2)
                .orElseGet(() -> "не удалось предположить диагноз");
    }
}
