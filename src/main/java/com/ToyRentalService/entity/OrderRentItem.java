package com.ToyRentalService.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class OrderRentItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private double price;

    private int quantity;

    private int daysToRent;

    @ManyToOne
    @JoinColumn(name = "order_rent_id")
    private OrderRent orderRent;

    @ManyToOne
    @JoinColumn(name = "toy_id")
    private Toy toy;
}
