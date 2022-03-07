package kda.learn.microservices.project.integrations.telegram;

import kda.learn.microservices.project.services.disease.DiseaseService;
import kda.learn.microservices.project.services.disease.model.Disease;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

@Component
public class UserMessageProcessor {

    private final DiseaseService diseaseService;
    private TgSender sender;

    public UserMessageProcessor(DiseaseService diseaseService) {
        this.diseaseService = diseaseService;
    }

    public void processMessage(String chatId, String text) {
        var diseases = diseaseService.guessDisease(text);
        for (var disease: diseases) {
            sender.send(createDiseaseMessage(chatId, disease));
        }
    }

    public void processCallbackQuery(CallbackQuery callbackQuery) {
        String data = callbackQuery.getData();

        sender.send(AnswerCallbackQuery
                .builder()
                .text(data) // TODO: вывести что-то полезное или совсем убрать
                .callbackQueryId(callbackQuery.getId())
                .build());

        sender.send(callbackQuery.getMessage().getChatId().toString(), "Далее будем искать лекарства и советы по лечению");
    }

    private SendMessage createDiseaseMessage(String chatId, Disease disease) {
        SendMessage res = new SendMessage();
        res.setChatId(chatId);
        res.enableHtml(true);
        res.setText(String.format("<b><u>%s</u></b>\n%s",
                disease.getName(),
                String.join(", ", disease.getSymptoms())));
        res.setReplyMarkup(createDiseaseKeyboard(disease));
        return res;
    }

    private InlineKeyboardMarkup createDiseaseKeyboard(Disease disease) {
        return new InlineKeyboardMarkup(
                List.of(List.of(createDiseaseButton(disease)))
        );
    }

    private InlineKeyboardButton createDiseaseButton(Disease disease) {
        var button = new InlineKeyboardButton();
        button.setText(disease.getName() + ": лечение");
        button.setCallbackData("disease:" + disease.getCode());
        return button;
    }

    // FIXME: Исправить кривую реализацию (TgBot <-> UserMessageProcessor)
    public void setSender(TgSender sender) {
        this.sender = sender;
    }
}
