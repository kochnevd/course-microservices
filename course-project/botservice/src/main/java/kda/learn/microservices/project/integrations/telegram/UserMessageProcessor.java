package kda.learn.microservices.project.integrations.telegram;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import kda.learn.microservices.project.services.disease.DiseaseService;
import kda.learn.microservices.project.services.disease.model.Disease;
import kda.learn.microservices.project.services.disease.model.TreatmentTips;
import kda.learn.microservices.project.services.drugs.DrugsService;
import kda.learn.microservices.project.services.drugs.model.DrugInfo;
import kda.learn.microservices.project.services.shops.ShopsService;
import kda.learn.microservices.project.services.shops.dto.ShopPrice;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.StringJoiner;

// TODO: вынести бизнес-логику в сервисный класс

@Component
public class UserMessageProcessor {

    public static final String CALLBACK_PREFIX_DISEASE = "disease:";
    public static final String CALLBACK_PREFIX_MEDICINES = "medicines:";
    public static final String CALLBACK_PREFIX_SHOPS = "shops:";
    public static final String CALLBACK_PREFIX_REFRESH_SHOPS = "refreshshops:";
    public static final String CALLBACK_PREFIX_SHOPPING = "shopping:";
    private static final TreatmentTips TIPS_NOT_FOUND = new TreatmentTips(null, "Советы по данной проблеме отсутствуют.");
    private static final int MAX_DRUG_COUNT_SHOWN = 5;

    private static final String DRUG_INFO_TEMPLATE = "<b>%s</b>\n%s";
    private static final DecimalFormat priceFormatter = new DecimalFormat("0.00");

    private final DiseaseService diseaseService;
    private final DrugsService drugsService;
    private final ShopsService shopsService;
    private final ShopsPricesCache shopsPricesCache;
    private final Counter counterTipsAsks;
    private final Counter counterTipsNotFound;
    private final Counter counterNlpCalls;
    private final Counter counterNlpMisunderstands;
    private final Map<String, Counter> countersDrugAbsentInShops = new HashMap<>();
    private final MeterRegistry registry;
    private TgSender tgSender;

    private final Map<String, InlineDrugMessages> inlineDrugMessages = new HashMap<>();

    public UserMessageProcessor(DiseaseService diseaseService, DrugsService drugsService, ShopsService shopsService, ShopsPricesCache shopsPricesCache,
                                MeterRegistry registry) {
        this.diseaseService = diseaseService;
        this.drugsService = drugsService;
        this.shopsService = shopsService;
        this.shopsPricesCache = shopsPricesCache;

        this.registry = registry;
        counterNlpCalls = registry.counter("Nlp-calls");
        counterNlpMisunderstands = registry.counter("Nlp-misunderstands");
        counterTipsAsks = registry.counter("Tips-asks");
        counterTipsNotFound = registry.counter("Tips-not-found");
    }

    public void processMessage(String chatId, String text) {
        if (!processMessageAsDrugName(chatId, text))
            processMessageAsSymptomsText(chatId, text);
    }

    private boolean processMessageAsDrugName(String chatId, String text) {
        return showMedicinesByDrugName(chatId, text);
    }

    private void processMessageAsSymptomsText(String chatId, String text) {
        counterNlpCalls.increment();
        var diseases = diseaseService.guessDisease(text);
        if (diseases.isEmpty()) {
            counterNlpMisunderstands.increment();
            String message;
            switch (new Random(System.currentTimeMillis()).nextInt(3)) {
                case 0:
                    message = "Да что вы говорите? " + Emoji.SCREAM + " Жалко я в этом не разбираюсь, а то бы что-нибудь посоветовал. Спросите еще что-нибудь, может догадаюсь?";
                    break;
                case 1:
                    message = "Как интересно, что бы это могло быть? " + Emoji.THINK + " Ума не приложу, можете дать подсказку?";
                    break;
                default:
                    message = "М-да, сложный случай... " + Emoji.EYEBROW + " Лучше спросите что-то попроще.";
                    break;
            }
            tgSender.send(chatId, message);
        }
        else for (var disease : diseases) {
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
            showMedicinesByDiseaseCode(chatId, diseaseCode);

        } else if (data.startsWith(CALLBACK_PREFIX_SHOPS)) {
            var drug = data.substring(CALLBACK_PREFIX_SHOPS.length());
            var inlineDrugMessage = checkInlineDrugMessages(chatId, messageId, drug);

            var prices = processIfPricesReady(drug, inlineDrugMessage);

            String alertText = prices == null ?
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
            var drug = data.substring(CALLBACK_PREFIX_SHOPPING.length());
            var prices = shopsPricesCache.getPrices(drug);

            String alertText;
            boolean showAlert;
            if (prices == null) {
                alertText = "Информация по ценам недоступна, попробуйте обновить данные";
                showAlert = false;
            }
            else if (prices.size() == 0){
                alertText = "К сожалению, препарат отсутствует в продаже, попробуйте зайти позже..";
                showAlert = false;
            }
            else {
                StringJoiner pricesInfo = new StringJoiner("\n");
                pricesInfo.add("Список аптек, где можно купить " + drug + ":");
                pricesInfo.add("");
                prices.forEach(shopPrice -> pricesInfo.add(String.format("%s: %s",
                        shopPrice.getShop(),
                        priceFormatter.format(shopPrice.getPrice()))));
                pricesInfo.add("");
                pricesInfo.add("Можно переходить к покупкам");
                alertText = pricesInfo.toString();
                showAlert = true;
            }
            tgSender.send(answerBuilder
                    .text(alertText)
                    .showAlert(showAlert)
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

    private Set<ShopPrice> processIfPricesReady(String drug, InlineDrugMessage inlineDrugMessage) {
        var prices = shopsPricesCache.getPrices(drug);
        if (prices != null) {
            // Попытка обновления на то же самое значение вызывает ошибку:
            //   Error editing message text: [400] Bad Request: message is not modified: specified new message content and reply markup are exactly the same as a current content and reply markup of the message
            String shopsInfo;
            var shopsCount = prices.size();
            if (shopsCount == 0) {
                shopsInfo = "К сожалению, в настоящее время " + drug + " в продаже отсутствует " + Emoji.SAD;
                countersDrugAbsentInShops.computeIfAbsent(
                        drug,
                        s -> Counter.builder("Drug-absent-in-shops").tag("drug", s).register(registry))
                    .increment();
            } else {
                var minPrice = prices.stream().min(Comparator.comparingDouble(ShopPrice::getPrice)).get().getPrice();
                var maxPrice = prices.stream().max(Comparator.comparingDouble(ShopPrice::getPrice)).get().getPrice();
                shopsInfo = String.format("Найдено <u>в %d %s</u>\n" +
                        "<i>Минимальная цена: <b>%s</b></i>\n" +
                        "<i>Максимальная цена: <b>%s</b></i>",
                        shopsCount, getCorrectShopWord(shopsCount), priceFormatter.format(minPrice), priceFormatter.format(maxPrice));
            }


            if (!shopsInfo.equals(inlineDrugMessage.shopsInfo)) {
                inlineDrugMessage.shopsInfo = shopsInfo;
                // поменять вид кнопки
                var message = updateDrugShopsMessage(inlineDrugMessage.chatId, inlineDrugMessage.messageId, drug, shopsInfo);
                tgSender.send(message);
            }
        }
        return prices;
    }

    private String getCorrectShopWord(int shopsCount) {
        if (shopsCount % 10 == 1 && shopsCount != 11)
            return "аптеке";
        else
            return "аптеках";
    }

    private boolean showMedicinesByDrugName(String chatId, String drugName) {
        var drugs = drugsService.findDrugs(drugName);
        if (drugs.isEmpty()) return false;

        var titleMessage = drugs.size() == 1 ? null : "Я нашел препараты с похожим названием:";
        ShowMedicines(chatId, drugs, titleMessage);
        return true;
    }

    private void showMedicinesByDiseaseCode(String chatId, String diseaseCode) {
        var drugs = drugsService.drugsForDisease(diseaseCode);
        if (drugs.isEmpty()) {
            tgSender.send(chatId, "Простите, я пока не знаю лекарства от этой болезни " + Emoji.CONFUSED +
                    "\nМожете посмотреть советы по лечению.");
            return;
        }

        ShowMedicines(chatId, drugs, "Могу порекомендовать такие препараты");
    }

    private void ShowMedicines(String chatId, List<DrugInfo> drugs, String titleMessage) {
        if (titleMessage != null) tgSender.send(chatId, titleMessage);
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
        counterTipsAsks.increment();
        var tips = diseaseService.findTreatmentTips(diseaseCode);
        if (tips == null) {
            tips = TIPS_NOT_FOUND;
            counterTipsNotFound.increment();
        }
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
