package com.ToyRentalService.entity;

import com.ToyRentalService.enums.OrderStatus;
import com.ToyRentalService.enums.OrderType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private double price;

    private int quantity;

    @Column(nullable = true)
    private int dayToRent;

    @ManyToOne
    @JoinColumn(name = "order_id")
    @JsonIgnore
    Orders orders;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @ManyToOne
    @JoinColumn(name = "post_id")
    Post post;

    @Enumerated(EnumType.STRING)
    private OrderType type;
}
