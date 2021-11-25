package kda.learn.microservices.hw2.storage;

import kda.learn.microservices.hw2.model.UserData;
import kda.learn.microservices.hw2.storage.entities.Model2EntityTransformer;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class UsersStorageDBImpl implements UsersStorage {

    @Autowired
    private UsersCRUDRepository dbRepository;

    @Override
    public UserData findUser(Long userId) {
        return Model2EntityTransformer.UserEntity2Model(dbRepository.findById(userId).orElse(null));
    }

    @Override
    public void createUser(UserData userData) {
        dbRepository.save(Model2EntityTransformer.UserModel2Entity(userData));
    }

    @Override
    public boolean updateUser(Long userId, UserData newUser) {
        if (dbRepository.existsById(userId)) {
            dbRepository.save(Model2EntityTransformer.UserModel2Entity(newUser));
            return true;
        }
        else
            return false;
    }

    @Override
    public boolean deleteUser(Long userId) {
        if (dbRepository.existsById(userId)) {
            dbRepository.deleteById(userId);
            return true;
        }
        else
            return false;
    }

    @Override
    public List<UserData> getUsers() {
        List<UserData> result = new ArrayList<>();
        dbRepository.findAll().forEach(user -> result.add(Model2EntityTransformer.UserEntity2Model(user)));
        return result;
    }
}
