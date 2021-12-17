package kda.learn.microservices.hw2.storage;

import kda.learn.microservices.hw2.model.UserData;
import kda.learn.microservices.hw2.storage.entities.Model2EntityTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

public class UsersStorageDBImpl implements UsersStorage {

    private static final Logger log = LoggerFactory.getLogger(UsersStorageDBImpl.class);

    @Autowired
    private UsersCRUDRepository dbRepository;

    // JUST FOR LOGGING

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.driverClassName}")
    private String dbDriver;

    @Value("${spring.jpa.database-platform}")
    private String dbPlatform;

    @PostConstruct
    public void init() {
        log.info("Using Database storage\n" +
                "\tURL={}\n" +
                "\tDriver={}\n" +
                "\tPlatform={}", dbUrl, dbDriver, dbPlatform);
    }

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

    @Override
    public void checkHealth() {
        //if (new java.util.Random().nextInt(10) == 5) throw new java.lang.RuntimeException("DB случайно упала");
        dbRepository.count();
    }
}
