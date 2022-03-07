package kda.learn.microservices.project.services.disease.model;

import java.util.ArrayList;
import java.util.List;

public class Disease {

    private final static List<String> EMPTY = new ArrayList<>(0);

    private String code;
    private String name;
    private List<String> symptoms = EMPTY;

    public String getCode() {
        return code;
    }

    public Disease code(String code) {
        this.code = code;
        return this;
    }

    public String getName() {
        return name;
    }

    public Disease name(String name) {
        this.name = name;
        return this;
    }

    public List<String> getSymptoms() {
        return symptoms;
    }

    public Disease symptoms(List<String> symptoms) {
        this.symptoms = symptoms;
        return this;
    }
}
