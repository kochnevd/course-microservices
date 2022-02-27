package kda.learn.microservices.project.integrations.telegram.commands;

public class HelpTextCommand extends AbstractTextCommand {

    public HelpTextCommand(String id, String description) {
        super(id, description);
    }

    @Override
    protected String getAnswerText() {
        return "Для получения консультации коротко опишите симптомы.\n" +
                "Для поиска конкретных лекарств воспользуйтесь соответствующей командой.";
    }
}
