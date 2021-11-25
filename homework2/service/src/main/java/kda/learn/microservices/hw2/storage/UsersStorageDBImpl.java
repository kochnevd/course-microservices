package kda.learn.microservices.hw2.storage;

import kda.learn.microservices.hw2.model.UserData;

import java.util.List;

public class UsersStorageDBImpl implements UsersStorage {
    @Override
    public UserData findUser(Long userId) {
        return null;
    }

    @Override
    public void createUser(UserData userData) {

    }

    @Override
    public boolean updateUser(Long userId, UserData newUser) {
        return false;
    }

    @Override
    public boolean deleteUser(Long userId) {
        return false;
    }

    @Override
    public List<UserData> getUsers() {
        return null;
    }
}
