package kda.learn.microservices.hw7;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.PAYMENT_REQUIRED)
public class BillingPaymentFailedException extends RuntimeException {
    public BillingPaymentFailedException(String message) {
        super(message);
    }
}
