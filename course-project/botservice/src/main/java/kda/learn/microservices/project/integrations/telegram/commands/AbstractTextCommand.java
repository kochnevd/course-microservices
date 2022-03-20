package kda.learn.microservices.project.integrations.telegram.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public abstract class AbstractTextCommand extends BotCommand {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public AbstractTextCommand(String commandIdentifier, String description) {
        super(commandIdentifier, description);
    }

    protected abstract String getAnswerText();

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        // формируем имя пользователя - поскольку userName может быть не заполнено, для этого случая используем имя и фамилию пользователя
        String userName = (user.getUserName() != null) ? user.getUserName() :
                String.format("%s %s", user.getLastName(), user.getFirstName());
        // формируем текст ответа
        String text = getAnswerText();
        // отправляем пользователю ответ
        sendAnswer(absSender, chat.getId(), this.getCommandIdentifier(), userName, text);
    }

    /**
     * Отправка ответа пользователю
     */
    void sendAnswer(AbsSender absSender, Long chatId, String commandName, String userName, String text) {
        SendMessage message = new SendMessage();
        //включаем поддержку режима разметки, чтобы управлять отображением текста и добавлять эмодзи
        message.enableMarkdown(true);
        message.setChatId(chatId.toString());
        message.setText(text);
        try {
            absSender.execute(message);
        } catch (TelegramApiException e) {
            log.error("сбой Telegram Bot API", e);
        }
    }
}
