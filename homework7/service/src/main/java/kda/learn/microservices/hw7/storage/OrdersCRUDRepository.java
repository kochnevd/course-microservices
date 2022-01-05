package kda.learn.microservices.hw7.storage;

import kda.learn.microservices.hw7.storage.entities.Order;
import org.springframework.data.repository.CrudRepository;

public interface OrdersCRUDRepository extends CrudRepository<Order, Long> {
}
