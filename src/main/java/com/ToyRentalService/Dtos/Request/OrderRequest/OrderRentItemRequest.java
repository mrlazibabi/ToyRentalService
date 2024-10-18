package com.ToyRentalService.Dtos.Request.OrderRequest;

import lombok.Data;

@Data
public class OrderRentItemRequest {
    private long postId;
    private int quantity;
    private int dayToRent;
}
