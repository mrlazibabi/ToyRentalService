package com.ToyRentalService.Dtos.Request.OrderRequest;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Data
@Getter
@Setter
public class OrderRentRequest {
    private int quantity;
    private int daysToRent;
    private Long postId;
}
