package kda.learn.microservices.hw7.controllers;

import kda.learn.microservices.hw7.dto.MessageReqDto;
import kda.learn.microservices.hw7.services.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
@RequestMapping("/")
public class NotificationsController {
    private static final Logger log = LoggerFactory.getLogger(OrdersController.class);

    private final NotificationService service;

    public NotificationsController(NotificationService service) {
        this.service = service;
    }

    @GetMapping(value = "/messages")
    public ResponseEntity<List<String>> getMessages() {
        return ResponseEntity.ok(
                service.getMessages()
        );
    }

    @PostMapping(value = "/message")
    public ResponseEntity<Void> sendMessage(@RequestBody MessageReqDto message) {
        log.info("CALL: sendMessage");
        service.sendMessage(message.getEmail(), message.getBody());
        return ResponseEntity.accepted().build();
    }
}
