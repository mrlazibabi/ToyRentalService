package com.ToyRentalService.Dtos.Request.OrderRequest;

import lombok.Data;

import java.util.List;
@Data
public class OrderRentRequest {
    private List<OrderRentItemRequest> item;
}
