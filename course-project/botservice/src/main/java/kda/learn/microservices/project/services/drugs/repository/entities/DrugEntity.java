package kda.learn.microservices.project.services.drugs.repository.entities;

public class DrugEntity {
    private String atxCode;
    private String name;
    private String instructions;

    public String getAtxCode() {
        return atxCode;
    }

    public void setAtxCode(String atxCode) {
        this.atxCode = atxCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }
}
