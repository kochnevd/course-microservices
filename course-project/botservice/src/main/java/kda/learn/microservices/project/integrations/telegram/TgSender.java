package kda.learn.microservices.project.integrations.telegram;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;

public interface TgSender {
    void send(BotApiMethod<?> method);

    void send(String chatId, String text);
}
