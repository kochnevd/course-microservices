package kda.learn.microservices.project.integrations.telegram;

import kda.learn.microservices.project.services.disease.DiseaseService;
import kda.learn.microservices.project.services.disease.model.Disease;
import kda.learn.microservices.project.services.disease.model.TreatmentTips;
import kda.learn.microservices.project.services.drugs.DrugsService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;
import java.util.Locale;

// TODO: вынести бизнес-логику в сервисный класс

@Component
public class UserMessageProcessor {

    public static final String CALLBACK_PREFIX_DISEASE = "disease:";
    public static final String CALLBACK_PREFIX_MEDICINES = "medicines:";
    private static final TreatmentTips TIPS_NOT_FOUND = new TreatmentTips("Советы по данной проблеме отсутствуют.");
    private static final int MAX_DRUG_COUNT_SHOWN = 5;

    private static final String DRUG_INFO_TEMPLATE = "<b>%s</b>\n%s";

    private final DiseaseService diseaseService;
    private final DrugsService drugsService;
    private TgSender tgSender;

    public UserMessageProcessor(DiseaseService diseaseService, DrugsService drugsService) {
        this.diseaseService = diseaseService;
        this.drugsService = drugsService;
    }

    public void processMessage(String chatId, String text) {
        var diseases = diseaseService.guessDisease(text);
        for (var disease: diseases) {
            tgSender.send(createDiseaseMessage(chatId, disease));
        }
    }

    public void processCallbackQuery(CallbackQuery callbackQuery) {
        String data = callbackQuery.getData();

        var answerBuilder = AnswerCallbackQuery
                .builder()
                .callbackQueryId(callbackQuery.getId());

        if (data.startsWith(CALLBACK_PREFIX_DISEASE)) {
            tgSender.send(answerBuilder
                .text("Загружаем советы по лечению")
                .build());
            showTreatmentTips(callbackQuery.getMessage().getChatId().toString(),
                    data.substring(CALLBACK_PREFIX_DISEASE.length()));
        }
        else if (data.startsWith(CALLBACK_PREFIX_MEDICINES)) {
            tgSender.send(answerBuilder
                    .text("Загружаем список препаратов")
                    .build());
            showMedicines(callbackQuery.getMessage().getChatId().toString(),
                    data.substring(CALLBACK_PREFIX_MEDICINES.length()));
        }
        else tgSender.send(answerBuilder
                    .text("Неизвестный код запроса: " + data)
                    .showAlert(true)
                    .build());
    }

    private void showMedicines(String chatId, String diseaseCode) {
        var drugs = drugsService.drugsForDisease(diseaseCode);
        if (drugs.isEmpty()) {
            tgSender.send(chatId, "Простите, я пока не знаю лекарства от этой болезни " + Emoji.CONFUSED);
            return;
        }

        tgSender.send(chatId, "Могу порекомендовать такие препараты");
        for (int i = 0; i < drugs.size(); i++) {
            if (i > MAX_DRUG_COUNT_SHOWN) break;

            var drugInfo = drugs.get(i);
            var text = String.format(DRUG_INFO_TEMPLATE, drugInfo.getName(), drugInfo.getInstructions());
            tgSender.send(chatId, text);
            // TODO: добавить кнопку для поиска в магазинах
        }
    }

    private void showTreatmentTips(String chatId, String diseaseCode) {
        var tips = diseaseService.findTreatmentTips(diseaseCode);
        if (tips == null) tips = TIPS_NOT_FOUND;
        tgSender.send(getTipsFormattedMessage(chatId, tips));
    }

    private BotApiMethod<?> getTipsFormattedMessage(String chatId, TreatmentTips tips) {
        StringBuilder res = new StringBuilder();
        tips.getUrls().forEach((url, description) -> res.append(description).append(": ").append(url).append("\n"));
        if (res.length() > 0) res.insert(0, "\n\n<b>Полезные ссылки:</b>\n");

        var message = new SendMessage(chatId, tips.getTipsText() + res.toString());
        message.enableHtml(true);
        return message;
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
                List.of(createDiseaseButton(disease))
        );
    }

    private List<InlineKeyboardButton> createDiseaseButton(Disease disease) {
        var adviceButton = new InlineKeyboardButton();
        adviceButton.setText(disease.getName() + ": лечение");
        adviceButton.setCallbackData(CALLBACK_PREFIX_DISEASE + disease.getCode());

        var medicinesButton = new InlineKeyboardButton();
        medicinesButton.setText("Препараты");
        medicinesButton.setCallbackData(CALLBACK_PREFIX_MEDICINES + disease.getCode());

        return List.of(adviceButton, medicinesButton);
    }

    // FIXME: Исправить кривую реализацию (TgBot <-> UserMessageProcessor)
    public void setTgSender(TgSender tgSender) {
        this.tgSender = tgSender;
    }
}
