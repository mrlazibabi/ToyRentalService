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

    private String image;

    @Positive(message = "Price must be positive")
    private double price;
}
