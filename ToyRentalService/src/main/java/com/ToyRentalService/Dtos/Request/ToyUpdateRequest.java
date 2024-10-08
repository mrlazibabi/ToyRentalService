package com.ToyRentalService.Dtos.Request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ToyUpdateRequest {
    private String toyName;

    private String category;

    @Min(value = 0, message = "Quantity must be non-negative")
    private int quantity;

    private String imageUrl;

    @NotBlank(message = "Description can not be blank!")
    private String description;

    @Positive(message = "Price must be positive")
    private double priceByTime;

    @Positive(message = "Price must be positive")
    private double depositFee;
}
