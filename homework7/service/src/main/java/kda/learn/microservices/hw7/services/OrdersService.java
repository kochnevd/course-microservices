package kda.learn.microservices.hw7.services;

import kda.learn.microservices.hw7.BillingPaymentFailedException;
import kda.learn.microservices.hw7.storage.Storage;
import kda.learn.microservices.hw7.storage.entities.Order;
import kda.learn.microservices.hw7.storage.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class OrdersService {

    private static final Logger log = LoggerFactory.getLogger(OrdersService.class);

    private final Storage storage;
    private final NotificationService notificationService;
    private final BillingService billingService;

    public OrdersService(Storage storage, NotificationService notificationService, BillingService billingService) {
        this.storage = storage;
        this.notificationService = notificationService;
        this.billingService = billingService;
    }

    public User createUser(User user) {
        User newUser = storage.createUser(user);
        var accountId = billingService.createAccount(newUser.getId());
        newUser.accountId(accountId);
        storage.updateUser(newUser);
        return newUser;
    }

    public Order createOrder(Order order) {
        User user = storage.getUser(order.getUserId());

        Order existingOrder = storage.findOrderByUuid(order.getUuid());
        if (existingOrder != null) {
            log.warn("Повторная попытка записи заказа uuid=" + existingOrder.getUuid());
            return existingOrder;
        }

        if (billingService.debitAccount(user.getAccountId(), order.getCost())) {
            notificationService.sendMessage(user.getEmail(),
                    String.format("Успешный заказ для пользователя '%s' на сумму %s: %s", user.getLogin(), order.getCost(), order.getOrderContent()));
            return storage.createOrder(order);
        } else {
            notificationService.sendMessage(user.getEmail(),
                    String.format("Не удалось оформить заказ для пользователя '%s' на сумму %s: платеж не прошел", user.getLogin(), order.getCost()));
            throw new BillingPaymentFailedException("Не удалось списать деньги со счета");
        }
    }
}
