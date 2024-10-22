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
    @JoinColumn(name = "post_id")
    private Post post;

    private int quantity;

    private int dayToRent; // Số ngày thuê, nếu loại đơn hàng là thuê

    private double price;

    @Enumerated(EnumType.STRING)
    private OrderType type; // BUYTOY, RENTTOY, BUYPOST
}
