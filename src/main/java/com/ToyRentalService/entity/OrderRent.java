package com.ToyRentalService.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "OrderRent")
@Getter
@Setter
public class OrderRent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private LocalDateTime createAt;
    private LocalDateTime startDate;
    private LocalDateTime expireDate;

    @ManyToOne
    @JoinColumn(name = "toyId")
    private Post toy;

    @ManyToOne
    @JoinColumn(name = "accountId")
    private Account account;

    private String address;
    private Double deposit;
}
