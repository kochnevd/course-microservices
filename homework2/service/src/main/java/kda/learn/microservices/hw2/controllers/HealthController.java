package kda.learn.microservices.hw2.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/health")
    public boolean healthCheck() {
        return true;
    }

}
