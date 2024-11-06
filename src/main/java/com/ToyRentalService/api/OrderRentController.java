package com.ToyRentalService.api;

import com.ToyRentalService.enums.OrderRentStatus;
import com.ToyRentalService.exception.exceptions.NotFoundException;
import com.ToyRentalService.service.OrderRentService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order-rent")
@SecurityRequirement(name = "api")
public class OrderRentController {
    @Autowired
    private OrderRentService orderRentService;

    @PostMapping("/return/{orderId}")
    public ResponseEntity<String> returnOrder(@PathVariable Long orderId) {
        try {
            orderRentService.returnOrder(orderId);
            return ResponseEntity.ok("Order returned successfully");
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order not found");
        }
    }
    @PutMapping("/deliver/{orderId}")
    public ResponseEntity<String> markOrderAsDelivered(@PathVariable Long orderId) {
        try {
            orderRentService.markOrderAsDelivered(orderId);
            return ResponseEntity.ok("Order marked as delivered.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    @PostMapping("/create")
    public ResponseEntity<String> createOrderRent(@RequestParam Long toyId, @RequestParam int quantity, @RequestParam int daysToRent) {
        try {
            String paymentUrl = orderRentService.createOrderRent(toyId, quantity, daysToRent);
            return ResponseEntity.ok(paymentUrl);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating order: " + e.getMessage());
        }
    }

    @PostMapping("/update-payment-status")
    public ResponseEntity<String> updateOrderRentStatus(@RequestParam long orderRentId, @RequestParam OrderRentStatus status) {
        try {
            orderRentService.updateOrderRentStatusAfterPayment(orderRentId, status);
            return ResponseEntity.ok("Order and payment status updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating order status: " + e.getMessage());
        }
    }
}
