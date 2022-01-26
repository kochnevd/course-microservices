package kda.learn.microservices.hw7.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.validation.annotation.Validated;

@Validated
public class MessageReqDto {

    @JsonProperty("email")
    private String email = null;

    @JsonProperty("body")
    private String body = null;

    public String getEmail() {
        return email;
    }

    public MessageReqDto email(String email) {
        this.email = email;
        return this;
    }

    public String getBody() {
        return body;
    }

    public MessageReqDto body(String body) {
        this.body = body;
        return this;
    }
}
