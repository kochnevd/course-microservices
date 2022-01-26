package kda.learn.microservices.hw7.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kda.learn.microservices.hw7.dto.MessageReqDto;
import kda.learn.microservices.hw7.services.NotificationService;
import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.TopicBuilder;

/**
 * For reference : https://www.maestralsolutions.com/spring-boot-implementation-for-apache-kafka-with-kafka-tool/
 */

@Configuration
public class KafkaConsumerConfig {

    private static final Logger log = LoggerFactory.getLogger(KafkaConsumerConfig.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String kafkaTopic = "my-topic";

    private final NotificationService service;

    public KafkaConsumerConfig(NotificationService service) {
        this.service = service;
    }

    @Bean
    public NewTopic topic() {
        return TopicBuilder.name(kafkaTopic)
                .partitions(10)
                .replicas(1)
                .build();
    }

    @KafkaListener(id = "myId", topics = kafkaTopic) // TODO: configure topic name
    public void consume(String in) {
        try {
            var message = deserializeResponse(in);
            service.sendMessage(message.getEmail(), message.getBody());
            log.info("Got message for " + message.getEmail());
        } catch (Exception e) {
            log.error("Failed ro process message <" + in + ">", e);
        }
    }

    private MessageReqDto deserializeResponse(String response) {
        try {
            return objectMapper.readValue(response, MessageReqDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Deserialize Error", e);
        }
    }

}