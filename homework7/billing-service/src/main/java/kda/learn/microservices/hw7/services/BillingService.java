package kda.learn.microservices.hw7.services;

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

    public Long createAccount(Long userId) {
        return storage.createAccount(userId);
    }

    public boolean debitAccount(Long accountId, BigDecimal sum) {
        return storage.debitAccount(accountId, sum);
    }

    public boolean depositAccount(Long accountId, BigDecimal sum) {
        return storage.depositAccount(accountId, sum);
    }
}
