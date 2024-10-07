package com.ToyRentalService.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "Toys")
public class Toy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private long toyId;

    @NotBlank(message = "ToyName can not be blank!")
    @Column(nullable = false, unique = true)
    private String toyName;

    @NotBlank(message = "Category can not be blank!")
    private String category;

    @Min(value = 0, message = "Quantity must be non-negative")
    private int quantity;

    private String image;

    @Positive(message = "Price must be positive")
    private double price;

    private boolean isDelete;
}
