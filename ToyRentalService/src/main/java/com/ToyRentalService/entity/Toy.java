package com.ToyRentalService.entity;

import com.ToyRentalService.enums.Status;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class Toy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
 
    @NotBlank(message = "ToyName can not be blank!")
    @Column(nullable = false, unique = true)
    private String toyName;

    @NotBlank(message = "Category can not be blank!")
    private String category;

    @Min(value = 0, message = "Quantity must be non-negative")
    private int quantity;

    private List<String> imageUrl;

    @NotBlank(message = "Description can not be blank!")
    private String description;

    @Positive(message = "Price must be positive")
    private double priceByTime;

    @Positive(message = "Price must be positive")
    private double depositFee;

    private boolean isDelete;

    private Status status;
}
