package kda.learn.microservices.hw2.storage.health;

import kda.learn.microservices.hw2.storage.UsersStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class StorageHealthIndicator implements HealthIndicator {

    private static final Logger log = LoggerFactory.getLogger(StorageHealthIndicator.class);

    private final UsersStorage storage;
    private Boolean lastHealthState;

    public StorageHealthIndicator(UsersStorage storage) {
        this.storage = storage;
        log.info("StorageHealthIndicator created");
    }

    @Override
    public Health getHealth(boolean includeDetails) {
        return health();
    }

    @Override
    public Health health() {
        try {
            storage.checkHealth();
            logState(true);
            return Health.up().build();
        } catch (Exception ex) {
            logState(false);
            return Health.down().withDetail("Description", "Storage readiness failed. " + ex).build();
        }
    }

    private void logState(boolean newState) {
        if (lastHealthState != null && lastHealthState.equals(newState))
            return;
        lastHealthState = newState;
        if (newState)
            log.info("Storage health is now OK !");
        else
            log.warn("Storage health is poor now...");
    }
}
