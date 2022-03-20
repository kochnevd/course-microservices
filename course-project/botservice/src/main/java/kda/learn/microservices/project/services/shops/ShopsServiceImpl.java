package kda.learn.microservices.project.services.shops;

import kda.learn.microservices.project.services.shops.dto.ShopPrice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

@Service
public class ShopsServiceImpl implements ShopsService {

    private static final int PARTNERS_COUNT = 5;
    private static final String[] PARTNERS = {"Рога и рожки", "Травка-муравка", "Колеса от Иваныча", "Не лечи лечёного", "От зари до зари"};

    private final Logger log = LoggerFactory.getLogger(ShopsServiceImpl.class);
    private final Random random = new Random(System.currentTimeMillis());

    @Override
    public void startSearchInShops(String medicine, ShopsSearchResultsReceiver resultsReceiver) {
        log.info("######## startSearchInShops called for {}", medicine);

        new Thread(() -> {
            try {
                Thread.sleep(500 + random.nextInt(20000));

                // TODO: эмуляция результатов запроса магазинов

                var shopCount = random.nextInt(PARTNERS_COUNT);
                var avgPriceKopeck = 10 + Math.abs(medicine.hashCode()) % 100000; // Чтоб примерно одинаковый диапазон цен для одного препарата
                var spacing = 10; // Разброс цен в %
                Set<ShopPrice> prices = new HashSet<>();

                for (int i = 0; i < shopCount; i++) {
                    var lowestPriceKopeck = avgPriceKopeck - avgPriceKopeck * spacing / 200;
                    double price = lowestPriceKopeck + random.nextInt(avgPriceKopeck * spacing / 100);
                    price = price / 100; // Переводим в рубли
                    prices.add(new ShopPrice(PARTNERS[i], price));
                }

                log.info("######## startSearchInShops FINISHED for {} ==> {}", medicine, prices);
                resultsReceiver.receiveSearchResults(medicine, prices);
            } catch (InterruptedException ignored) {}
        }).start();
    }
}
