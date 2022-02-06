package kda.learn.microservices.project.integrations.telegram;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Класс для обработки сообщений бота.
 * Ссылка на бот: http://t.me/the_cure_bot
 * Управление ботом: https://t.me/botfather
 * Описание Bot API: https://core.telegram.org/bots/api
 *
 * В создании бота помогла статья: https://habr.com/ru/post/528694/
 */
@Component
public class TgBot extends TelegramLongPollingCommandBot {

    @Value("${telegram.bot.token}")
    private String bot_token;

    @Value("${telegram.bot.username}")
    private String bot_username;

    private final Logger log = LoggerFactory.getLogger(TgBot.class);

    public TgBot() {
        super();
        log.info("################### TgBot created");
        // Регистрируем команды
        // TODO register(command)

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

        Message msg = update.getMessage();
        Long chatId = msg.getChatId();
        String userName = getUserName(msg);

        setAnswer(chatId, userName, "################### my answer");
    }

    /**
     * Формирование имени пользователя
     * @param msg сообщение
     */
    private String getUserName(Message msg) {
        User user = msg.getFrom();
        String userName = user.getUserName();
        return (userName != null) ? userName : String.format("%s %s", user.getLastName(), user.getFirstName());
    }

    /**
     * Отправка ответа
     * @param chatId id чата
     * @param userName имя пользователя
     * @param text текст ответа
     */
    private void setAnswer(Long chatId, String userName, String text) {
        SendMessage answer = new SendMessage();
        answer.setText(text);
        answer.setChatId(chatId.toString());
        try {
            execute(answer);
        } catch (TelegramApiException e) {
            //логируем сбой Telegram Bot API, используя userName
        }
    }

}
