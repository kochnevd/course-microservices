package kda.learn.microservices.hw7.storage;

import kda.learn.microservices.hw7.storage.entities.Account;
import kda.learn.microservices.hw7.storage.entities.Order;
import kda.learn.microservices.hw7.storage.entities.User;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.stream.StreamSupport;

@Component
public class Storage {

    private final UsersCRUDRepository usersRepository;
    private final OrdersCRUDRepository ordersRepository;
    private final AccountsCRUDRepository accountsCRUDRepository;

    public Storage(UsersCRUDRepository usersRepository, OrdersCRUDRepository ordersRepository, AccountsCRUDRepository accountsCRUDRepository) {
        this.usersRepository = usersRepository;
        this.ordersRepository = ordersRepository;
        this.accountsCRUDRepository = accountsCRUDRepository;
    }

    public User createUser(User user) {
        return usersRepository.save(user);
    }

    public void createAccount(Integer userId) {
        accountsCRUDRepository.save(
                new Account()
                        .setUserId(userId)
        );
    }

    public Order createOrder(Order order) {
        return ordersRepository.save(order);
    }

    public boolean debitAccount(long userId, BigDecimal cost) {
        var account = StreamSupport.stream(
                    accountsCRUDRepository
                    .findAll()
                    .spliterator(), false)
                .filter(acc -> acc.getUserId() == userId)
                .findAny()
                .orElse(null);
        if (account == null) {
            // аккаунт не существует
            return false;
        }

        double balance = account.getBalance().doubleValue();
        if (balance < cost.doubleValue()) {
            // недостаточно денег
            return false;
        } else {
            account.setBalance(BigDecimal.valueOf(balance - cost.doubleValue()));
            accountsCRUDRepository.save(account);
            return true;
        }
    }
}
