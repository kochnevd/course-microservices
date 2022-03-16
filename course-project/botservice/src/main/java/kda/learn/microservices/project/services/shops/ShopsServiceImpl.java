package kda.learn.microservices.project.services.shops;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class ShopsServiceImpl implements ShopsService {

    private final Logger log = LoggerFactory.getLogger(ShopsServiceImpl.class);

    @Override
    public void startSearchInShops(String medicine, ShopsSearchResultsReceiver resultsReceiver) {
        log.info("######## startSearchInShops called for {}", medicine);
        // TODO: implement опрос магазинов

        new Thread(() -> {
            try {
                Thread.sleep(500 + new Random().nextInt(30000));
                resultsReceiver.receiveSearchResults(medicine);
            } catch (InterruptedException ignored) {}
        }).start();
    }
}
