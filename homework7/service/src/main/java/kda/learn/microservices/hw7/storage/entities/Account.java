package kda.learn.microservices.hw7.storage.entities;

import java.math.BigDecimal;

import javax.persistence.*;

@Entity
@Table(name = "accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id")
    private Integer userId;

    @Column
    private BigDecimal balance = BigDecimal.ZERO;

    public Integer getId() {
        return id;
    }

    public Integer getUserId() {
        return userId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public Account setUserId(Integer userId) {
        this.userId = userId;
        return this;
    }

    public Account setBalance(BigDecimal balance) {
        this.balance = balance;
        return this;
    }
}
