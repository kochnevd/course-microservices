package kda.learn.microservices.hw7.services;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NotificationService {

    private final List<String> messages = new ArrayList<>();

    public List<String> getMessages() {
        return messages;
    }

    public void sendMessage(String email, String body) {
        messages.add(email + ": " + body);
    }
}
