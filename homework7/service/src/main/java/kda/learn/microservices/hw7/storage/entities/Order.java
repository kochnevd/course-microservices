package kda.learn.microservices.hw7.storage.entities;

import java.math.BigDecimal;

import javax.persistence.*;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "order_content")
    private String orderContent;

    @Column
    private BigDecimal cost;
}
