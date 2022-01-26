package kda.learn.microservices.hw7.integrations.notifications;

import kda.learn.microservices.hw7.dto.MessageReqDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Service
public final class NotificationsKafkaProducer {
    private static final Logger logger = LoggerFactory.getLogger(NotificationsKafkaProducer.class);

    @Value("${notification.topic}")
    private String kafkaTopic;

    private final KafkaTemplate<String, MessageReqDto> kafkaTemplate;

    public NotificationsKafkaProducer(KafkaTemplate<String, MessageReqDto> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(MessageReqDto message) {

        var future = this.kafkaTemplate.send(kafkaTopic, message);

        future.addCallback(new ListenableFutureCallback<>() {
            @Override
            public void onFailure(Throwable ex) {
                logger.info("Unable to send message for [{}] due to : {}", message.getEmail(), ex.getMessage());
            }

            @Override
            public void onSuccess(SendResult<String, MessageReqDto> result) {
                logger.info("Sent message for [{}] with offset=[{}]", message.getEmail(), result.getRecordMetadata().offset());
            }
        });
    }
}
