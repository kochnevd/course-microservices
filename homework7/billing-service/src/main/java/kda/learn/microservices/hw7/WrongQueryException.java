package kda.learn.microservices.hw7;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class WrongQueryException extends RuntimeException {
    public WrongQueryException(String message) {
        super(message);
    }
}
