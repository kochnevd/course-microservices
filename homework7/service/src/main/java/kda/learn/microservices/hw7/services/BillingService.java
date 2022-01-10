package kda.learn.microservices.hw7.services;

import kda.learn.microservices.hw7.WrongQueryException;
import kda.learn.microservices.hw7.storage.Storage;
import kda.learn.microservices.hw7.storage.entities.Account;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class BillingService {

    private final Storage storage;

    public BillingService(Storage storage) {
        this.storage = storage;
    }

    public List<Account> getAccounts() {
        return storage.getAccounts();
    }

    public void createAccount(Long userId) {
        storage.createAccount(userId);
    }

    public boolean debitAccount(Long userId, BigDecimal sum) {
        return storage.debitAccount(userId, sum);
    }

    public void depositAccount(Long accountId, BigDecimal sum) {
        if (!storage.depositAccount(accountId, sum))
            throw new WrongQueryException("Не удалось внести деньги на счет");
    }
}
