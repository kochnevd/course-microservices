package kda.learn.microservices.hw7.services;

import kda.learn.microservices.hw7.integrations.notifications.NotificationsKafkaProducer;
import kda.learn.microservices.hw7.transformers.NotificationsTransformer;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private final NotificationsKafkaProducer kafkaProducer;

    public NotificationService(NotificationsKafkaProducer kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
    }

    public void sendMessage(String email, String body) {
        kafkaProducer.sendMessage(NotificationsTransformer.transformToDto(body, email));
    }
}
