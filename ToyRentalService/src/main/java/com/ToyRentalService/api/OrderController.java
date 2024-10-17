package com.ToyRentalService.api;

import com.ToyRentalService.Dtos.Request.OrderRequest.OrderBuyRequest;
import com.ToyRentalService.Dtos.Request.OrderRequest.OrderPostTicketRequest;
import com.ToyRentalService.Dtos.Request.OrderRequest.OrderRentRequest;
import com.ToyRentalService.entity.Orders;
import com.ToyRentalService.service.OrderService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/order")
@SecurityRequirement(name = "api")
public class OrderController {

    @Autowired
    OrderService orderService;

    @PostMapping("buy")
    public ResponseEntity createOrderBuy(@RequestBody OrderBuyRequest orderBuyRequest){
        Orders orders = orderService.createOrderBuy(orderBuyRequest);
        return ResponseEntity.ok(orders);
    }

    @PostMapping("rent")
    public ResponseEntity createOrderRent(@RequestBody OrderRentRequest orderRentRequest){
        Orders orders = orderService.createOrderRent(orderRentRequest);
        return ResponseEntity.ok(orders);
    }

    @PostMapping("postTicket")
    public ResponseEntity createOrderPostTicket(@RequestBody OrderPostTicketRequest orderPostTicketRequest){
        Orders orders = orderService.createOrderPostTicket(orderPostTicketRequest);
        return ResponseEntity.ok(orders);
    }
}
