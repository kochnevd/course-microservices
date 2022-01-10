package kda.learn.microservices.hw7.storage;

import kda.learn.microservices.hw7.storage.entities.Account;
import kda.learn.microservices.hw7.storage.entities.Order;
import kda.learn.microservices.hw7.storage.entities.User;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
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

    public void createAccount(Long userId) {
        accountsCRUDRepository.save(
                new Account()
                        .setUserId(userId)
        );
    }

    public Order createOrder(Order order) {
        return ordersRepository.save(order);
    }

    public boolean debitAccount(Long userId, BigDecimal sum) {
        var account = StreamSupport.stream(
                    accountsCRUDRepository
                    .findAll()
                    .spliterator(), false)
                .filter(acc -> acc.getUserId().equals(userId))
                .findAny()
                .orElse(null);
        if (account == null) {
            // аккаунт не существует
            return false;
        }

        double balance = account.getBalance().doubleValue();
        if (balance < sum.doubleValue()) {
            // недостаточно денег
            return false;
        } else {
            account.setBalance(BigDecimal.valueOf(balance - sum.doubleValue()));
            accountsCRUDRepository.save(account);
            return true;
        }
    }

    public boolean depositAccount(Long accountId, BigDecimal sum) {
        var account = accountsCRUDRepository.findById(accountId).orElse(null);
        if (account == null) return false;

        double balance = account.getBalance().doubleValue();
        account.setBalance(BigDecimal.valueOf(balance + sum.doubleValue()));
        accountsCRUDRepository.save(account);

        return true;
    }

    public List<Account> getAccounts() {
        return StreamSupport
                .stream(accountsCRUDRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }
}
