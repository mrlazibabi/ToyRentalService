package com.ToyRentalService.Dtos.Request.ToyRequest;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import java.util.Set;

@Data
public class ToyRentRequest {

    @NotBlank(message = "Toy name cannot be blank")private String toyName;

    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;

    @URL(message = "Invalid URL format for image URL")
    private String imageUrl;

    @Size(max = 500, message = "Description can be at most 500 characters")
    private String description;

    @NotNull(message = "Price by day cannot be null")
    @Min(value = 0, message = "Price by day must be positive")
    private double priceByDay;

    @Positive(message = "DepositFee must be positive")
    private double depositFee;

    Set<Long> categoryId;
}
