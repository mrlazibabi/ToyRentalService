//package com.ToyRentalService.api;
//
//import com.ToyRentalService.Dtos.Request.OrderRequest.OrderBuyRequest;
//import com.ToyRentalService.Dtos.Request.OrderRequest.OrderPostTicketRequest;
//import com.ToyRentalService.Dtos.Request.OrderRequest.OrderRentRequest;
//import com.ToyRentalService.entity.Orders;
//import com.ToyRentalService.service.OrderService;
//import io.swagger.v3.oas.annotations.security.SecurityRequirement;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@CrossOrigin("*")
//@RequestMapping("/api/order")
//@SecurityRequirement(name = "api")
//public class OrderController {
//
//    @Autowired
//    OrderService orderService;
//
//    @PostMapping("buy")
//    public ResponseEntity createOrderBuy(@RequestBody OrderBuyRequest orderBuyRequest){
//        Orders orders = orderService.createOrderBuy(orderBuyRequest);
//        return ResponseEntity.ok(orders);
//    }
//
//    @PostMapping("rent")
//    public ResponseEntity createOrderRent(@RequestBody OrderRentRequest orderRentRequest){
//        Orders orders = orderService.createOrderRent(orderRentRequest);
//        return ResponseEntity.ok(orders);
//    }
//
//    @PostMapping("postTicket")
//    public ResponseEntity createOrderPostTicket(@RequestBody OrderPostTicketRequest orderPostTicketRequest){
//        Orders orders = orderService.createOrderPostTicket(orderPostTicketRequest);
//        return ResponseEntity.ok(orders);
//    }
//    @PostMapping("payment")
//    public ResponseEntity<String> create(@RequestParam long orderId) throws Exception {
//        String vnPayURL = orderService.createUrl(orderId);
//        return ResponseEntity.ok(vnPayURL);
//    }
//
//
//}


package com.ToyRentalService.api;
import com.ToyRentalService.Dtos.Request.OrderRequest.OrderPostTicketRequest;
import com.ToyRentalService.entity.OrderHistory;
import com.ToyRentalService.entity.Orders;
import com.ToyRentalService.enums.OrderStatus;
import com.ToyRentalService.enums.OrderType;
import com.ToyRentalService.service.OrderService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/orders")
@SecurityRequirement(name = "api")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/create-from-cart")
    public ResponseEntity<String> createOrderFromCart() {
        try {
            String paymentUrl = orderService.createOrderFromCart();
            return ResponseEntity.ok(paymentUrl);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to create order: " + e.getMessage());
        }
    }
    @PostMapping("/buy-post")
    public ResponseEntity<String> createOrderForPost(@RequestBody OrderPostTicketRequest orderPostTicketRequest) {
        try {
            Orders order = orderService.createOrderPostTicket(orderPostTicketRequest);
            String paymentUrl = orderService.createUrl(order.getId());
            return ResponseEntity.ok(paymentUrl);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to create order for post: " + e.getMessage());
        }
    }

    @GetMapping("/history/type")
    public List<OrderHistory> getOrderHistoryByType(@RequestParam("type") OrderType type) {
        return orderService.getOrderHistoryByType(type);
    }

    @GetMapping("/history")
    public List<OrderHistory> getOrderHistoryByAccount() {
        return orderService.getOrderHistoryForCurrentUser();
    }
    @PostMapping("/update-status")
    public ResponseEntity<String> updateOrderStatusAfterPayment(@RequestParam long orderId, @RequestParam OrderStatus status) {
        try {
            orderService.updateOrderStatusAfterPayment(orderId, status);

            if (status == OrderStatus.COMPLETED) {
                orderService.updatePostCountAfterPayment(orderId);
            }
            if (status == OrderStatus.COMPLETED) {
                orderService.updateStockAfterPayment(orderId);
            }

            return ResponseEntity.ok("Order status and relevant data updated successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update order status: " + e.getMessage());
        }
    }
}

