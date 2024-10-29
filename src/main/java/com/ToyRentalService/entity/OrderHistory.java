package com.ToyRentalService.entity;

import com.ToyRentalService.enums.OrderStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderHistory
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private Date orderDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private String description;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Orders order;


}
