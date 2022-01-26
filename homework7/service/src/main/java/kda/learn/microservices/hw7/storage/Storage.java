package kda.learn.microservices.hw7.storage;

import kda.learn.microservices.hw7.storage.entities.Order;
import kda.learn.microservices.hw7.storage.entities.User;
import org.springframework.stereotype.Component;

import java.util.stream.StreamSupport;

@Component
public class Storage {

    private final UsersCRUDRepository usersRepository;
    private final OrdersCRUDRepository ordersRepository;

    public Storage(UsersCRUDRepository usersRepository, OrdersCRUDRepository ordersRepository) {
        this.usersRepository = usersRepository;
        this.ordersRepository = ordersRepository;
    }

    public User createUser(User user) {
        return usersRepository.save(user);
    }

    public Order createOrder(Order order) {
        return ordersRepository.save(order);
    }

    public void updateUser(User user) {
        usersRepository.save(user);
    }

    public User getUser(Long userId) {
        return usersRepository.findById(userId).orElse(null);
    }

    public Order findOrderByUuid(String uuid) {
        if (uuid == null) return null;
        return StreamSupport.stream(ordersRepository.findAll().spliterator(), true)
                .filter(order -> uuid.equals(order.getUuid()))
                .findAny()
                .orElse(null);
    }
}
