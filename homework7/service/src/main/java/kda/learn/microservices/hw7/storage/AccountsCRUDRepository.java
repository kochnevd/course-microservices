package kda.learn.microservices.hw7.storage;

import kda.learn.microservices.hw7.storage.entities.Account;
import org.springframework.data.repository.CrudRepository;

public interface AccountsCRUDRepository extends CrudRepository<Account, Long> {
}
