package com.ToyRentalService.Dtos.Request.OrderRequest;

import lombok.Data;

import java.util.List;
@Data
public class OrderBuyRequest {
    private List<OrderBuyItemRequest> item;
}
