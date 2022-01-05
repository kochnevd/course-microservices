package kda.learn.microservices.hw7.storage;

import kda.learn.microservices.hw7.storage.entities.Account;
import kda.learn.microservices.hw7.storage.entities.User;
import org.springframework.stereotype.Component;

@Component
public class Storage {

    private final UsersCRUDRepository usersRepository;
    private final AccountsCRUDRepository accountsCRUDRepository;

    public Storage(UsersCRUDRepository usersRepository, AccountsCRUDRepository accountsCRUDRepository) {
        this.usersRepository = usersRepository;
        this.accountsCRUDRepository = accountsCRUDRepository;
    }

    public User createUser(User user) {
        return usersRepository.save(user);
    }

    public void createAccount(Integer userId) {
        accountsCRUDRepository.save(
                new Account()
                        .setUserId(userId)
        );
    }
}
