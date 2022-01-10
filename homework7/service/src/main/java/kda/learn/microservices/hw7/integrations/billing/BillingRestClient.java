package kda.learn.microservices.hw7.integrations.billing;

import kda.learn.microservices.hw7.WrongQueryException;
import kda.learn.microservices.hw7.dto.AccountReqDto;
import kda.learn.microservices.hw7.dto.AccountRespDto;
import kda.learn.microservices.hw7.dto.DebitReqDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

@Component
public class BillingRestClient {
    private final RestTemplate restTemplate;

    @Value("${billing.base-url}")
    private String billingServiceBaseUrl;

    public BillingRestClient() {
        restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));
    }

    public Long createAccount(Long userId) {
        var request = new AccountReqDto().userId(userId);
        HttpStatus statusCode;
        String message;
        Long accountId = null;
        try {
            ResponseEntity<AccountRespDto> response = restTemplate.postForEntity(
                    billingServiceBaseUrl + "/account",
                    request,
                    AccountRespDto.class
            );
            accountId = response.getBody().getId();
            statusCode = response.getStatusCode();
            message = response.toString();
        } catch (HttpStatusCodeException e) {
            statusCode = e.getStatusCode();
            message = e.getStatusText();
        }
        switch (statusCode) {
            case CREATED:
                return accountId;
            case BAD_REQUEST:
                throw new WrongQueryException(message);
            default:
                throw new RuntimeException("Unexpected response: " + statusCode + " " + message);
        }
    }

    public boolean debitAccount(Long accountId, BigDecimal sum) {
        var request = new DebitReqDto().accountId(accountId).sum(sum);
        HttpStatus statusCode;
        String message;
        try {
            ResponseEntity<Void> response = restTemplate.postForEntity(
                    billingServiceBaseUrl + "/debit",
                    request,
                    Void.class
            );
            statusCode = response.getStatusCode();
            message = response.toString();
        } catch (HttpStatusCodeException e) {
            statusCode = e.getStatusCode();
            message = e.getStatusText();
        }
        switch (statusCode) {
            case ACCEPTED:
                return true;
            case PAYMENT_REQUIRED:
                return false;
            case BAD_REQUEST:
                throw new WrongQueryException(message);
            default:
                throw new RuntimeException("Unexpected response: " + statusCode + " " + message);
        }
    }
}
