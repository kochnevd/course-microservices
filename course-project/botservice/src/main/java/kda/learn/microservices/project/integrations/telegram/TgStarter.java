package kda.learn.microservices.project.integrations.telegram;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.LongPollingBot;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import javax.annotation.PostConstruct;

@Component
public class TgStarter {

    private static final Logger log = LoggerFactory.getLogger(TgStarter.class);

    private final LongPollingBot bot;

    private boolean started;

    public TgStarter(LongPollingBot bot) {
        this.bot = bot;
    }

    @PostConstruct
    private void StartBot() {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(bot);
            log.info("Bots API initialized successfully");
            started = true;
        } catch (TelegramApiException e) {
            log.error("Bots API initialization failed", e);
        }

    }

    public boolean isStarted() {
        return started;
    }
}
