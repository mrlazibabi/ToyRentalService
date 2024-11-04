package com.ToyRentalService.entity;

import com.ToyRentalService.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Payment")
@Getter
@Setter
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "orderId")
    private Orders order;

    private float price;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    private Boolean isDeposit;
}
