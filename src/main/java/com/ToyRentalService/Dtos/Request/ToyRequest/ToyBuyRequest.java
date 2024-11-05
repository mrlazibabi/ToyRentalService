package com.ToyRentalService.Dtos.Request.ToyRequest;

import lombok.Data;

import java.util.Set;
@Data
public class ToyBuyRequest {

    private String toyName;

    private int quantity;

    private String imageUrl;

    private String description;

    private double price;

    Set<Long> categoryId;
}
