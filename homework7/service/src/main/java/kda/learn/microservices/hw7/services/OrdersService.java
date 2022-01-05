package kda.learn.microservices.hw7.services;

import kda.learn.microservices.hw7.storage.Storage;
import kda.learn.microservices.hw7.storage.entities.User;
import org.springframework.stereotype.Service;

@Service
public class OrdersService {

    private final Storage storage;

    public OrdersService(Storage storage) {
        this.storage = storage;
    }

    public User createUser(User user) {
        User newUser = storage.createUser(user);
        storage.createAccount(newUser.getId());
        return newUser;
    }
}
