package kda.learn.microservices.project.services.shops;

import org.springframework.stereotype.Service;

@Service
public interface ShopsService {
    void startSearchInShops(String medicine, ShopsSearchResultsReceiver resultsReceiver);
}
