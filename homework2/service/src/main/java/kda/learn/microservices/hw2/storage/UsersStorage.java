package kda.learn.microservices.hw2.storage;

import kda.learn.microservices.hw2.model.UserData;

import java.util.List;

public interface UsersStorage {

    UserData findUser(Long userId);

    void createUser(UserData userData);

    boolean updateUser(Long userId, UserData newUser);

    boolean deleteUser(Long userId);

    List<UserData> getUsers();
}
