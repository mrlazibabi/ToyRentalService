package com.ToyRentalService.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "OrderBuy")
@Getter
@Setter
public class OrderBuy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime createAt;
    private String status;

    @ManyToOne
    @JoinColumn(name = "toyId")
    private Toy toy;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Account customer;

    private String address;
    private Double totalPrice;
}
