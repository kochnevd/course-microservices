package kda.learn.microservices.hw7.controllers;

import kda.learn.microservices.hw7.services.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
@RequestMapping("/")
public class NotificationsController {

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
}
