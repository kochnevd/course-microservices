package kda.learn.microservices.project.services.shops.dto;

public class ShopPrice {
    private final String shop;
    private final double price;

    public ShopPrice(String shop, double price) {
        this.shop = shop;
        this.price = price;
    }

    public String getShop() {
        return shop;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return String.format("ShopPrice{'%s': %s}", shop, price);
    }
}
