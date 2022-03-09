package kda.learn.microservices.project.integrations.telegram;

import kda.learn.microservices.project.integrations.telegram.commands.HelpTextCommand;
import kda.learn.microservices.project.integrations.telegram.commands.StartTextCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Класс для обработки сообщений бота.
 */
@Component
public class TgBot extends TelegramLongPollingCommandBot implements TgSender {

    private final Logger log = LoggerFactory.getLogger(TgBot.class);

    private final UserMessageProcessor messageProcessor;

    @Value("${telegram.bot.token}")
    private String bot_token;

    @Value("${telegram.bot.username}")
    private String bot_username;

    public TgBot(UserMessageProcessor messageProcessor) {
        super();
        log.info("################### TgBot created");

        //класс для работы с текстовыми сообщениями, не являющимися командами
        this.messageProcessor = messageProcessor;
        messageProcessor.setTgSender(this);

        // Регистрируем команды
        register(new StartTextCommand("start", "Старт"));
        register(new HelpTextCommand("help", "Справка"));
    }

    @Override
    public String getBotUsername() {
        return bot_username;
    }

    @Override
    public String getBotToken() {
        return bot_token;
    }

    @Override
    public void onRegister() {
        log.info("################### onRegister");
    }

    /**
     * Ответ на запрос, не являющийся командой
     */
    @Override
    public void processNonCommandUpdate(Update update) {
        log.info("################### processNonCommandUpdate ({})", update);

        if (update.hasCallbackQuery()) {
            messageProcessor.processCallbackQuery(update.getCallbackQuery());
        }
        else if (update.hasMessage()) {
            Message msg = update.getMessage();
            var chatId = msg.getChatId().toString();
            messageProcessor.processMessage(chatId, msg.getText());
        }
        else {
            log.warn("## Unknown update received: {}", update);
        }
    }

    @Override
    public void send(BotApiMethod<?> message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("сбой Telegram Bot API", e);
        }
    }

    @Override
    public void send(String chatId, String text) {
        // TODO: дубль с AbstractTextCommand.sendAnswer
        SendMessage message = new SendMessage();
        //включаем поддержку режима разметки, чтобы управлять отображением текста и добавлять эмодзи
        message.enableMarkdownV2(true);
        message.setChatId(chatId);
        message.setText(text);
        send(message);
    }

}
