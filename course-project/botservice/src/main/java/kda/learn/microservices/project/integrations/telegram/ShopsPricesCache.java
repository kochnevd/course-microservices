package kda.learn.microservices.project.integrations.telegram;

import kda.learn.microservices.project.services.shops.ShopsSearchResultsReceiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Component
public class ShopsPricesCache implements ShopsSearchResultsReceiver {

    private final Logger log = LoggerFactory.getLogger(ShopsPricesCache.class);

    private final Map<String, ShopPrices> cache = new ConcurrentHashMap<>();
    private final Map<String, List<Consumer<String>>> subscribers = new HashMap<>();

    public void subscribeForDrug(String drug, Consumer<String> onDrugPricesReady) {
        var drugSubscriberList = subscribers.computeIfAbsent(drug, k -> new ArrayList<>());
        drugSubscriberList.add(onDrugPricesReady);
        if (cache.containsKey(drug)) // Данные поступили до подписки, можем сразу выдать
            onDrugPricesReady.accept(drug);
    }

    @Override
    public void receiveSearchResults(String medicine) {
        log.info("######## receiveSearchResults called for {}", medicine);
        updatePricesInCache(medicine);
    }

    private void updatePricesInCache(String medicine) {
        cache.put(medicine, new ShopPrices("My price info"));
        notifySubscribers(medicine);
    }

    private void notifySubscribers(String medicine) {
        var drugSubscribers = subscribers.get(medicine);
        if (drugSubscribers != null) {
            drugSubscribers.forEach(consumer -> consumer.accept(medicine));
        }
    }

    public Object getPrices(String drug) {
        var res = cache.get(drug);
        log.info("######## getPrices called for {} ==> {}", drug, res);
        return res;
    }

    static class ShopPrices {
        String priceInfo;

        public ShopPrices(String priceInfo) {
            this.priceInfo = priceInfo;
        }
    }
}
