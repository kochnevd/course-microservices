package kda.learn.microservices.hw7.services;

import kda.learn.microservices.hw7.BillingPaymentFailedException;
import kda.learn.microservices.hw7.storage.Storage;
import kda.learn.microservices.hw7.storage.entities.Order;
import kda.learn.microservices.hw7.storage.entities.User;
import org.springframework.stereotype.Service;

@Service
public class OrdersService {

    private final Storage storage;
    private final BillingService billingService;

    public OrdersService(Storage storage, BillingService billingService) {
        this.storage = storage;
        this.billingService = billingService;
    }

    public User createUser(User user) {
        User newUser = storage.createUser(user);
        billingService.createAccount(newUser.getId());
        return newUser;
    }

    public Order createOrder(Order order) {
        if (billingService.debitAccount(order.getUserId(), order.getCost()))
            return storage.createOrder(order);

        throw new BillingPaymentFailedException("Не удалось списать деньги со счета");
    }
}
