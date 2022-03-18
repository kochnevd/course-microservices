package kda.learn.microservices.project.integrations.telegram;

import kda.learn.microservices.project.services.disease.DiseaseService;
import kda.learn.microservices.project.services.disease.model.Disease;
import kda.learn.microservices.project.services.disease.model.TreatmentTips;
import kda.learn.microservices.project.services.drugs.DrugsService;
import kda.learn.microservices.project.services.drugs.model.DrugInfo;
import kda.learn.microservices.project.services.shops.ShopsService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

// TODO: вынести бизнес-логику в сервисный класс

@Component
public class UserMessageProcessor {

    public static final String CALLBACK_PREFIX_DISEASE = "disease:";
    public static final String CALLBACK_PREFIX_MEDICINES = "medicines:";
    public static final String CALLBACK_PREFIX_SHOPS = "shops:";
    public static final String CALLBACK_PREFIX_REFRESH_SHOPS = "refreshshops:";
    public static final String CALLBACK_PREFIX_SHOPPING = "shopping:";
    private static final TreatmentTips TIPS_NOT_FOUND = new TreatmentTips("Советы по данной проблеме отсутствуют.");
    private static final int MAX_DRUG_COUNT_SHOWN = 5;

    private static final String DRUG_INFO_TEMPLATE = "<b>%s</b>\n%s";

    private final DiseaseService diseaseService;
    private final DrugsService drugsService;
    private final ShopsService shopsService;
    private final ShopsPricesCache shopsPricesCache;
    private TgSender tgSender;

    private final Map<String, InlineDrugMessages> inlineDrugMessages = new HashMap<>();

    public UserMessageProcessor(DiseaseService diseaseService, DrugsService drugsService, ShopsService shopsService, ShopsPricesCache shopsPricesCache) {
        this.diseaseService = diseaseService;
        this.drugsService = drugsService;
        this.shopsService = shopsService;
        this.shopsPricesCache = shopsPricesCache;
    }

    public void processMessage(String chatId, String text) {
        var diseases = diseaseService.guessDisease(text);
        for (var disease : diseases) {
            tgSender.send(createDiseaseMessage(chatId, disease));
        }
    }

    public void processCallbackQuery(CallbackQuery callbackQuery) {
        var data = callbackQuery.getData();
        var chatId = callbackQuery.getMessage().getChatId().toString();
        var messageId = callbackQuery.getMessage().getMessageId();

        var answerBuilder = AnswerCallbackQuery
                .builder()
                .callbackQueryId(callbackQuery.getId());

        if (data.startsWith(CALLBACK_PREFIX_DISEASE)) {
            var diseaseCode = data.substring(CALLBACK_PREFIX_DISEASE.length());
            tgSender.send(answerBuilder
                    .text("Загружаем советы по лечению")
                    .build());
            showTreatmentTips(chatId, diseaseCode);

        } else if (data.startsWith(CALLBACK_PREFIX_MEDICINES)) {
            var diseaseCode = data.substring(CALLBACK_PREFIX_MEDICINES.length());
            tgSender.send(answerBuilder
                    .text("Загружаем список препаратов")
                    .build());
            showMedicines(chatId, diseaseCode);

        } else if (data.startsWith(CALLBACK_PREFIX_SHOPS)) {
            var drug = data.substring(CALLBACK_PREFIX_SHOPS.length());
            var inlineDrugMessage = checkInlineDrugMessages(chatId, messageId, drug);

            var pricesReady = processIfPricesReady(drug, inlineDrugMessage);

            String alertText = !pricesReady ?
                    "Опрашиваем партнеров в поисках препарата " + drug :
                    "Можно отображать аптеки с ценами!";

            tgSender.send(answerBuilder
                    .text(alertText)
                    .build());

        } else if (data.startsWith(CALLBACK_PREFIX_REFRESH_SHOPS)) {
            var drug = data.substring(CALLBACK_PREFIX_REFRESH_SHOPS.length());
            checkInlineDrugMessages(chatId, messageId, drug); // На случай перезапуска бота надо обновить список inline-клавиатур

            tgSender.send(answerBuilder
                    .text("Опрашиваем партнеров в поисках препарата " + drug)
                    .build());

        } else if (data.startsWith(CALLBACK_PREFIX_SHOPPING)) {

            tgSender.send(answerBuilder
                    .text("Можно переходить к покупкам")
                    .build());

        } else tgSender.send(answerBuilder
                .text("Неизвестный код запроса: " + data)
                .showAlert(true)
                .build());
    }

    private InlineDrugMessage checkInlineDrugMessages(String chatId, Integer messageId, String drug) {
        var allDrugMessages = inlineDrugMessages.computeIfAbsent(drug, InlineDrugMessages::new);

        var res = allDrugMessages.inlineDrugMessages
                .stream()
                .filter(inlineDrugMessage -> inlineDrugMessage.match(chatId, messageId))
                .findAny()
                .orElse(null);
        if (res == null) {
            res = new InlineDrugMessage(chatId, messageId);
            allDrugMessages.inlineDrugMessages.add(res);
            shopsPricesCache.subscribeForDrug(drug, chatId, this::onDrugPricesReady); // На случай перезапуска бота надо снова подписаться
        }
        shopsService.startSearchInShops(drug, shopsPricesCache);

        return res;
    }

    private boolean processIfPricesReady(String drug, InlineDrugMessage inlineDrugMessage) {
        var prices = shopsPricesCache.getPrices(drug);
        if (prices == null)
            return false;
        else {
            // Попытка обновления на то же самое значение вызывает ошибку:
            //   Error editing message text: [400] Bad Request: message is not modified: specified new message content and reply markup are exactly the same as a current content and reply markup of the message
            var shopsInfo = "Здесь будет краткая информация по наличию и ценам..";
            if (!shopsInfo.equals(inlineDrugMessage.shopsInfo)) {
                inlineDrugMessage.shopsInfo = shopsInfo;
                // поменять вид кнопки
                var message = updateDrugShopsMessage(inlineDrugMessage.chatId, inlineDrugMessage.messageId, drug, shopsInfo);
                tgSender.send(message);
            }
            return true;
        }
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
            var message = createDrugShopsMessage(chatId, drugInfo);
            tgSender.send(message);
            // запускаем поиск в магазинах, не дожидаясь нажатия на кнопку пользователем
            shopsService.startSearchInShops(drugInfo.getName(), shopsPricesCache);
            shopsPricesCache.subscribeForDrug(drugInfo.getName(), chatId, this::onDrugPricesReady);
        }
    }

    private void onDrugPricesReady(String drug) {
        System.out.println("###### onDrugPricesReady: " + drug);
        inlineDrugMessages.values()
                .stream()
                .filter(inlineDrugMessages -> inlineDrugMessages.drug.equalsIgnoreCase(drug))
                .forEach(inlineDrugMessages -> inlineDrugMessages.inlineDrugMessages.forEach(
                        inlineDrugMessage -> processIfPricesReady(inlineDrugMessages.drug, inlineDrugMessage)
                ));
    }

    private BotApiMethod<?> createDrugShopsMessage(String chatId, DrugInfo drugInfo) {
        SendMessage res = new SendMessage();
        res.setChatId(chatId);
        res.enableHtml(true);
        res.setText(String.format(DRUG_INFO_TEMPLATE, drugInfo.getName(), drugInfo.getInstructions()));
        res.setReplyMarkup(createSearchDrugShopsKeyboard(drugInfo.getName()));
        return res;
    }

    private BotApiMethod<?> updateDrugShopsMessage(String chatId, Integer messageId, String drug, String shopsInfo) {
        var res = new EditMessageText();
        res.setChatId(chatId);
        res.setMessageId(messageId);
        res.enableHtml(true);
        res.setText(String.format(DRUG_INFO_TEMPLATE, drug, shopsInfo));
        res.setReplyMarkup(createReadyDrugShopsKeyboard(drug));
        return res;
    }

    private InlineKeyboardMarkup createSearchDrugShopsKeyboard(String drug) {
        return new InlineKeyboardMarkup(List.of(
                createSearchDrugShopsButton(drug)
        ));
    }

    private InlineKeyboardMarkup createReadyDrugShopsKeyboard(String drug) {
        return new InlineKeyboardMarkup(List.of(List.of(
                createDrugShoppingKeyboard(drug),
                createDrugShopsRefreshButton(drug)
        )));
    }

    private List<InlineKeyboardButton> createSearchDrugShopsButton(String drug) {
        var shopsButton = new InlineKeyboardButton();
        shopsButton.setText(drug + " в продаже");
        shopsButton.setCallbackData(CALLBACK_PREFIX_SHOPS + drug);
        return List.of(shopsButton);
    }

    private InlineKeyboardButton createDrugShoppingKeyboard(String drug) {
        var shopsButton = new InlineKeyboardButton();
        shopsButton.setText(drug + ": к покупкам..");
        shopsButton.setCallbackData(CALLBACK_PREFIX_SHOPPING + drug);
        return shopsButton;
    }

    private InlineKeyboardButton createDrugShopsRefreshButton(String drug) {
        var button = new InlineKeyboardButton();
        button.setText("Обновить данные");
        button.setCallbackData(CALLBACK_PREFIX_REFRESH_SHOPS + drug);
        return button;
    }

    /////////////////////////////////////////////////////////

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

    /////////////////////////////////////////////////////////

    private static class InlineDrugMessage {
        private final String chatId;
        private final Integer messageId;
        private String shopsInfo;

        public InlineDrugMessage(String chatId, Integer messageId) {
            this.chatId = chatId;
            this.messageId = messageId;
        }

        public boolean match(String chatId, Integer messageId) {
            return this.chatId.equals(chatId) && this.messageId.equals(messageId);
        }
    }

    private static class InlineDrugMessages {
        public String drug;
        private final List<InlineDrugMessage> inlineDrugMessages = new ArrayList<>();

        public InlineDrugMessages(String drug) {
            this.drug = drug;
        }
    }
}
