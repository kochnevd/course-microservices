package kda.learn.microservices.hw2.services;

import kda.learn.microservices.hw2.model.UserData;
import kda.learn.microservices.hw2.storage.UsersStorage;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsersService {

    private final UsersStorage storage;

    public UsersService(UsersStorage storage) {
        this.storage = storage;
    }

    public List<UserData> getUsers() {
        return storage.getUsers();
    }

    public UserData findUser(Long userId) {
        return storage.findUser(userId);
    }

    public void createUser(UserData userData) {
        storage.createUser(userData);
    }

    public boolean updateUser(Long userId, UserData newUser) {
        if (!newUser.getId().equals(userId))
            throw new RuntimeException("Inconsistent userId");
        return storage.updateUser(userId, newUser);
    }

    public boolean deleteUser(Long userId) {
        return storage.deleteUser(userId);
    }
}
