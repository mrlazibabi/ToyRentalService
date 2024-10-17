package com.ToyRentalService.Dtos.Request.OrderRequest;

import lombok.Data;

@Data
public class OrderBuyItemRequest {
    private long postId;
    private int quantity;
}
