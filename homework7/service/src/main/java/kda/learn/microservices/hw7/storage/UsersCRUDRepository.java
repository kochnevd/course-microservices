package kda.learn.microservices.hw7.storage;

import kda.learn.microservices.hw7.storage.entities.User;
import org.springframework.data.repository.CrudRepository;

public interface UsersCRUDRepository extends CrudRepository<User, Long> {
}
