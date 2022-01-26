package kda.learn.microservices.hw7.storage.entities;

import java.math.BigDecimal;

import javax.persistence.*;

@Entity
@Table(name = "accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column
    private BigDecimal balance = BigDecimal.ZERO;

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public Account setUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public Account setBalance(BigDecimal balance) {
        this.balance = balance;
        return this;
    }
}
