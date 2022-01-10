package kda.learn.microservices.hw7.services;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class BillingService {

    public Long createAccount(Long userId) {
        // TODO: call billing service
        throw new RuntimeException("Not implemented");
    }

    public boolean debitAccount(Long accountId, BigDecimal sum) {
        // TODO: call billing service
        throw new RuntimeException("Not implemented");
    }
}
