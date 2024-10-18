package com.ToyRentalService.Dtos.Request.PostRequest;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.Set;
@Data
public class PostBuyRequest {

    private String toyName;

    private int quantity;

    private String imageUrl;

    private String description;

    private double price;

    Set<Long> categoryId;
}
