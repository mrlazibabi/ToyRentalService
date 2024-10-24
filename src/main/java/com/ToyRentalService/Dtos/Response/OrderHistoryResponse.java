package com.ToyRentalService.Dtos.Response;

import com.ToyRentalService.entity.OrderItem;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Data
@Getter
@Setter
public class OrderHistoryResponse {
    private long postId;
    private String orderType;
    private double totalPrice;
    private String orderDate;
}
