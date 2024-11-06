package com.ToyRentalService.entity;

import com.ToyRentalService.enums.OrderType;
import com.ToyRentalService.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "Payment")
@Getter
@Setter
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "orderId")
    private Orders order;
    Date createAt;

    private float price;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @Enumerated(EnumType.STRING)
    private OrderType orderType;

    private Boolean isDeposit;
    @OneToOne
    @JoinColumn(name = "order_rent_id")
    private OrderRent orderRent;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Account customer;

}
