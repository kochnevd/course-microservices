package kda.learn.microservices.project.services.disease.model;

import java.util.LinkedHashMap;

public class TreatmentTips {
    private final String diseaseCode;
    private final String tipsText;
    private final LinkedHashMap<String, String> urls = new LinkedHashMap<>();

    public TreatmentTips(String diseaseCode, String tipsText) {
        this.diseaseCode = diseaseCode;
        this.tipsText = tipsText;
    }

    public String getDiseaseCode() {
        return diseaseCode;
    }

    public String getTipsText() {
        return tipsText;
    }

    public LinkedHashMap<String, String> getUrls() {
        return urls;
    }

    public void addUrl(String url, String description) {
        urls.put(url, description);
    }
}
