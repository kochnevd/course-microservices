package kda.learn.microservices.hw2.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UsersController {

    @GetMapping("/users")
    public List<String> getUsers() {
        return List.of("User1", "User2");
    }

}
