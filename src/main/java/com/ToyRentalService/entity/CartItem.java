package com.ToyRentalService.entity;

import com.ToyRentalService.enums.OrderType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    @JsonIgnore
    private Cart cart;


    @ManyToOne
    @JoinColumn(name = "toy_id")
    private Toy toy;

    private int quantity;

    private int dayToRent;

    private double price;

    @Enumerated(EnumType.STRING)
    private OrderType type;
}
