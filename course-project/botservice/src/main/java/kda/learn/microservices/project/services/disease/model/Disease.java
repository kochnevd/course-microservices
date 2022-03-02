package kda.learn.microservices.project.services.disease.model;

public class Disease {

    private String code;
    private String name;

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

}
