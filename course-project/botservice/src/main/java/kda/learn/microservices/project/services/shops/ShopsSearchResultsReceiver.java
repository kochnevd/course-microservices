package kda.learn.microservices.project.services.shops;

import kda.learn.microservices.project.services.shops.dto.ShopPrice;

import java.util.Set;

public interface ShopsSearchResultsReceiver {
    void receiveSearchResults(String medicine, Set<ShopPrice> shopPrices);
}
