package com.ToyRentalService.entity;

import com.ToyRentalService.enums.PostType;
import com.ToyRentalService.enums.Status;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
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
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
 
    
    @Column(nullable = false)
    private String toyName;

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
    private PostType postType;

    @Enumerated(EnumType.STRING)
    private Status status;
    @ManyToMany
    @JoinTable(name = "Post_Category",
            joinColumns = @JoinColumn(name = "post_id"),
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

    @OneToMany(mappedBy = "post")
    @JsonIgnore
    List<OrderItem> orderItems;


    @ManyToOne
    @JoinColumn(name = "customer_id")
    @JsonIgnore
    private Account customer;

    @OneToMany(mappedBy = "post")
    @JsonIgnore
    Set<Feedback> post_feedbacks;


    public void decrementQuantity(int amount) {
        if (this.quantity >= amount) {
            this.quantity -= amount;
        } else {
            throw new IllegalArgumentException("Not enough stock available.");
        }
    }
}
