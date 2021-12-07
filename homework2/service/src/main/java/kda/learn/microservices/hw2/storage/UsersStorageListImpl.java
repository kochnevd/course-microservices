package kda.learn.microservices.hw2.storage;

import kda.learn.microservices.hw2.model.UserData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

public class UsersStorageListImpl implements UsersStorage {

    private final List<UserData> storage = new ArrayList<>();
    private static final Logger log = LoggerFactory.getLogger(UsersStorageListImpl.class);

    @PostConstruct
    public void init() {
        log.info("Using list-in-memory storage");
    }

    @Override
    public UserData findUser(Long userId) {
        return storage.stream().filter(user -> user.getId().equals(userId)).findAny().orElse(null);
    }

    @Override
    public void createUser(UserData user) {
        if (user.getId() == null) user.setId((long) storage.size()+1);
        storage.add(user);
    }

    @Override
    public boolean updateUser(Long userId, UserData newUser) {
        for (int i = 0; i < storage.size(); i++) {
            if (storage.get(i).getId().equals(userId)) {
                storage.set(i, newUser);
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean deleteUser(Long userId) {
        return storage.removeIf(user -> user.getId().equals(userId));
    }

    @Override
    public List<UserData> getUsers() {
        return storage;
    }

    @Override
    public void checkHealth() {
        // always OK
    }
}
