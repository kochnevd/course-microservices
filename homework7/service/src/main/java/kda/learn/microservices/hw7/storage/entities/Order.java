package kda.learn.microservices.hw7.storage.entities;

import java.math.BigDecimal;

import javax.persistence.*;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "order_content")
    private String orderContent;

    @Column
    private BigDecimal cost;

    @Column(name = "uuid")
    private String uuid;

    public Long getId() {
        return id;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public String getOrderContent() {
        return orderContent;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUuid() {
        return uuid;
    }

    public Order setUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public Order setOrderContent(String orderContent) {
        this.orderContent = orderContent;
        return this;
    }

    public Order setCost(BigDecimal cost) {
        this.cost = cost;
        return this;
    }

    public Order setUuid(String uuid) {
        this.uuid = uuid;
        return this;
    }
}
