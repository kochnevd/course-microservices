package kda.learn.microservices.project.services.nlp;

import java.util.List;

public interface NlpSymptomsService {
    List<String> guessDisease(String text);
}
