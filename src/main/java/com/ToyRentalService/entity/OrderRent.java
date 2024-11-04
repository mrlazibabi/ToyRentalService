package com.ToyRentalService.entity;

import com.ToyRentalService.enums.OrderRentStatus;
import com.ToyRentalService.enums.OrderStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Table(name = "OrderRent")
@Entity
@Getter
@Setter
public class OrderRent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private Date createdAt;
    private double totalPrice;
    private int rentalDays;
    private Date deliveredAt;
    private Date dueDate;
    private Date returnDate;

    @Enumerated(EnumType.STRING)
    private OrderRentStatus status;
    private double lateFee;
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Account customer;

    @OneToMany(mappedBy = "orderRent", cascade = CascadeType.ALL)
    private List<OrderRentItem> orderRentItems;
}
