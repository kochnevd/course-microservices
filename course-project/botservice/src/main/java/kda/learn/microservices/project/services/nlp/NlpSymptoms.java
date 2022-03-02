package kda.learn.microservices.project.services.nlp;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NlpSymptoms {
    public List<String> guessDisease(String text) {
        var res = new ArrayList<String>();
        res.add("toothache");
        res.add("headache");
        return res; // TODO: implement
    }
}
