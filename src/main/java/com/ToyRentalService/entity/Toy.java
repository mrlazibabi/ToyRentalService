package com.ToyRentalService.entity;

import com.ToyRentalService.enums.ToyType;
import com.ToyRentalService.enums.Status;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

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

    @NotBlank(message = "ToyName can not be blank!")
    @Column(nullable = false)
    private String toyName;

    private String fromUser;

    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;

    @URL(message = "Invalid URL format for image URL")
    private String imageUrl;

    @Size(max = 500, message = "Description can be at most 500 characters")
    private String description;


    @Min(value = 0, message = "Price must be a positive value")
    @Column(nullable = true)
    private double price;

    @Min(value = 0, message = "Price by day must be a positive value")
    @Column(nullable = true)
    private double priceByDay;

    @Min(value = 0, message = "Deposit fee must be a positive value")
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
