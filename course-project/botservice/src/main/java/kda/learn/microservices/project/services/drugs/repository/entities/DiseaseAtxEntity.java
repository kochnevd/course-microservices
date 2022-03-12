package kda.learn.microservices.project.services.drugs.repository.entities;

import java.util.Set;

public class DiseaseAtxEntity {
    private String diseaseCode;
    private Set<String> atxCodes;

    public String getDiseaseCode() {
        return diseaseCode;
    }

    public void setDiseaseCode(String diseaseCode) {
        this.diseaseCode = diseaseCode;
    }

    public Set<String> getAtxCodes() {
        return atxCodes;
    }

    public void setAtxCodes(Set<String> atxCodes) {
        this.atxCodes = atxCodes;
    }
}
