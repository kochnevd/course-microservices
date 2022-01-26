package kda.learn.microservices.hw7.transformers;

import kda.learn.microservices.hw7.dto.MessageReqDto;

public class NotificationsTransformer {
    public static MessageReqDto transformToDto(String body, String email) {
        return new MessageReqDto()
                .body(body)
                .email(email);
    }
}
