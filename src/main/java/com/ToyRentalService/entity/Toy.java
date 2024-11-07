package com.ToyRentalService.entity;

import com.ToyRentalService.enums.ToyType;
import com.ToyRentalService.enums.Status;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Toy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
 
    
    @Column(nullable = false)
    private String toyName;

    private String fromUser;

    private int quantity;
    
    private String imageUrl;

    private String description;

    @Column(nullable = true)
    private double price;


    @Column(nullable = true)
    private double priceByDay;


    @Column(nullable = true)
    private double depositFee;

    private boolean isDelete;

    @Enumerated(EnumType.STRING)
    private ToyType toyType;

    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToMany
    @JoinTable(name = "Toy_Category",
            joinColumns = @JoinColumn(name = "toy_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
            )
    @JsonIgnore
    Set<Category> categories = new HashSet<>();
    @ManyToMany
    @JoinTable(name = "order_post",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "order_id"))
    private List<Orders> orders;
    @ManyToMany
    Set<Account> accounts;

    @OneToMany(mappedBy = "toy")
    @JsonIgnore
    List<OrderItem> orderItems;


    @ManyToOne
    @JoinColumn(name = "customer_id")
    @JsonIgnore
    private Account customer;

    @OneToMany(mappedBy = "toy")
    @JsonIgnore
    Set<Feedback> toy_feedbacks;


    public void decrementQuantity(int amount) {
        if (this.quantity >= amount) {
            this.quantity -= amount;
        } else {
            throw new IllegalArgumentException("Not enough stock available.");
        }
    }
}
