package kda.learn.microservices.hw7.services;

import kda.learn.microservices.hw7.storage.AccountsCRUDRepository;
import kda.learn.microservices.hw7.storage.entities.Account;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class BillingService {

    private final AccountsCRUDRepository accountsCRUDRepository;

    public BillingService(AccountsCRUDRepository accountsCRUDRepository) {
        this.accountsCRUDRepository = accountsCRUDRepository;
    }

    public List<Account> getAccounts() {
        return StreamSupport
                .stream(accountsCRUDRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }
}
