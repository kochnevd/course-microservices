package kda.learn.microservices.hw7.storage;

import kda.learn.microservices.hw7.storage.entities.Account;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
public class Storage {

    private final AccountsCRUDRepository accountsCRUDRepository;

    public Storage(AccountsCRUDRepository accountsCRUDRepository) {
        this.accountsCRUDRepository = accountsCRUDRepository;
    }

    public Long createAccount(Long userId) {
        return accountsCRUDRepository.save(
                new Account()
                        .setUserId(userId)
        ).getId();
    }

    public boolean debitAccount(Long accountId, BigDecimal sum) {
        var account = accountsCRUDRepository.findById(accountId).orElse(null);
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
