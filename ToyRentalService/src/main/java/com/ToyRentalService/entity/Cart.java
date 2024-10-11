package com.ToyRentalService.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Cart")
@Getter
@Setter
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer quantity;
    private Double totalPrice;
    private String type;

    @ManyToOne
    @JoinColumn(name = "accountId")
    private Account account;
}
