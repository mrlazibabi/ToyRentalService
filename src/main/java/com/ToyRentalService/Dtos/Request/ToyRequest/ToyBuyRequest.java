package com.ToyRentalService.Dtos.Request.ToyRequest;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import java.util.Set;
@Data
public class ToyBuyRequest {

    @NotBlank(message = "Toy name cannot be blank")
    private String toyName;

    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;

    @URL(message = "Invalid URL format for image URL")
    private String imageUrl;

    @Size(max = 500, message = "Description can be at most 500 characters")
    private String description;

    @NotNull(message = "Price cannot be null")
    @Min(value = 0, message = "Price must be positive")
    private double price;

    Set<Long> categoryId;
}
