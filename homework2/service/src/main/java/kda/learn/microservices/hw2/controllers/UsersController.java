package kda.learn.microservices.hw2.controllers;

import kda.learn.microservices.hw2.dto.UserDto;
import kda.learn.microservices.hw2.services.UsersService;
import kda.learn.microservices.hw2.transformers.UserDtoTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UsersController {

    private static final Logger log = LoggerFactory.getLogger(UsersController.class);
    private final UsersService service;

    public UsersController(UsersService service) {
        this.service = service;
    }

    @GetMapping
    public List<UserDto> getUsers() {
        log.info("CALL: getUsers");
        return service.getUsers().stream()
                .map(UserDtoTransformer::transformToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUser(@PathVariable("userId") Long userId) {
        log.info("CALL: getUser({})", userId);
        var foundUser = service.findUser(userId);
        if (foundUser == null)
            return ResponseEntity.notFound().build();
        else
            return ResponseEntity.ok(UserDtoTransformer.transformToDto(foundUser));
    }

    @PostMapping
    public ResponseEntity<Void> createUser(@Valid @RequestBody UserDto userDto) {
        log.info("CALL: createUser");
        service.createUser(UserDtoTransformer.transformFromDto(userDto));
        return ResponseEntity.ok(null);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<Void> updateUser(@PathVariable("userId") Long userId, @Valid @RequestBody UserDto newUser) {
        log.info("CALL: updateUser({})", userId);
        if (service.updateUser(userId, UserDtoTransformer.transformFromDto(newUser)))
            return ResponseEntity.ok(null);
        else
            return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable("userId") Long userId) {
        log.info("CALL: deleteUser({})", userId);
        if (service.deleteUser(userId))
            return ResponseEntity.ok(null);
        else
            return ResponseEntity.notFound().build();
    }

}
