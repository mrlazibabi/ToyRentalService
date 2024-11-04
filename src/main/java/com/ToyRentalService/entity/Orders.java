package com.ToyRentalService.entity;

import com.ToyRentalService.enums.OrderStatus;
import com.ToyRentalService.enums.OrderType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "Orders")
@Getter
@Setter
public class Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private Date createAt;

    @Enumerated(EnumType.STRING)
    private OrderType type;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private double totalPrice;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    @JsonIgnore
    private Account customer;

    @OneToMany(mappedBy = "orders", cascade = CascadeType.ALL)
    List<OrderItem> orderItems;
}
