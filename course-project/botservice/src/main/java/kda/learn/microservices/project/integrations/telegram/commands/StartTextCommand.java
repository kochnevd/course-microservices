package kda.learn.microservices.project.integrations.telegram.commands;

public class StartTextCommand extends AbstractTextCommand {

    public StartTextCommand(String id, String description) {
        super(id, description);
    }

    @Override
    protected String getAnswerText() {
        // TODO: вынести текстовки в ресурсы
        return "Бот-лечебник приветствует Вас!\nЯ готов Вас выслушать.\n\nДля получения справки по работе с ботом нажмите /help";
    }
}
