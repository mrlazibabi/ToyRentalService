package com.ToyRentalService.Dtos.Response;

import com.ToyRentalService.entity.OrderItem;
import com.ToyRentalService.entity.Post;
import com.ToyRentalService.enums.OrderType;
import lombok.*;

import java.util.Date;
import java.util.List;
@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderHistoryResponse {
    private long orderId;
    private List<Post> posts;
    private OrderType orderType;
    private double totalPrice;
    private Date orderDate;
}
