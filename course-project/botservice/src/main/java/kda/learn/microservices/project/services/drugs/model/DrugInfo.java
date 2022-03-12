package kda.learn.microservices.project.services.drugs.model;

public class DrugInfo {
    private String atxCode;
    private String name;
    private String instructions;

    public String getAtxCode() {
        return atxCode;
    }

    public DrugInfo atx(String atx) {
        this.atxCode = atx;
        return this;
    }

    public String getName() {
        return name;
    }

    public DrugInfo name(String name) {
        this.name = name;
        return this;
    }

    public String getInstructions() {
        return instructions;
    }

    public DrugInfo instructions(String instructions) {
        this.instructions = instructions;
        return this;
    }
}
