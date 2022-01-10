package kda.learn.microservices.hw7.services;

import kda.learn.microservices.hw7.integrations.billing.BillingRestClient;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class BillingService {

    private final BillingRestClient restClient;

    public BillingService(BillingRestClient restClient) {
        this.restClient = restClient;
    }

    public Long createAccount(Long userId) {
        return restClient.createAccount(userId);
    }

    public boolean debitAccount(Long accountId, BigDecimal sum) {
        return restClient.debitAccount(accountId, sum);
    }
}
