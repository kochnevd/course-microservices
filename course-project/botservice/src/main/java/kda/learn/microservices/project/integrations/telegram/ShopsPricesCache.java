package kda.learn.microservices.project.integrations.telegram;

import kda.learn.microservices.project.services.shops.ShopsSearchResultsReceiver;
import kda.learn.microservices.project.services.shops.dto.ShopPrice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Component
public class ShopsPricesCache implements ShopsSearchResultsReceiver {

    private final Logger log = LoggerFactory.getLogger(ShopsPricesCache.class);

    private final Map<String, Set<ShopPrice>> cache = new ConcurrentHashMap<>();
    private final Map<String, List<Subscriber>> subscribers = new HashMap<>();

    public void subscribeForDrug(String drug, String chatId, Consumer<String> onDrugPricesReady) {
        var drugSubscriberList = subscribers.computeIfAbsent(drug, k -> new ArrayList<>());
        if (drugSubscriberList.stream().noneMatch(subscriber -> subscriber.charId.equals(chatId)))
            drugSubscriberList.add(new Subscriber(chatId, onDrugPricesReady));
        if (cache.containsKey(drug)) // Данные поступили до подписки, можем сразу выдать
            onDrugPricesReady.accept(drug);
    }

    @Override
    public void receiveSearchResults(String medicine, Set<ShopPrice> shopPrices) {
        log.info("######## receiveSearchResults called for {}", medicine);
        updatePricesInCache(medicine, shopPrices);
    }

    private void updatePricesInCache(String medicine, Set<ShopPrice> shopPrices) {
        cache.put(medicine, shopPrices);
        notifySubscribers(medicine);
    }

    private void notifySubscribers(String medicine) {
        var drugSubscribers = subscribers.get(medicine);
        if (drugSubscribers != null) {
            drugSubscribers.forEach(subscriber -> subscriber.eventConsumer.accept(medicine));
        }
    }

    public Set<ShopPrice> getPrices(String drug) {
        var res = cache.get(drug);
        log.info("######## getPrices called for {} ==> {}", drug, res);
        return res;
    }

    private static class Subscriber {
        String charId;
        Consumer<String> eventConsumer;

        public Subscriber(String charId, Consumer<String> eventConsumer) {
            this.eventConsumer = eventConsumer;
            this.charId = charId;
        }
    }
}
