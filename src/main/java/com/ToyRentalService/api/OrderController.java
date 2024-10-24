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
import com.ToyRentalService.entity.Orders;
import com.ToyRentalService.service.OrderService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/orders")
@SecurityRequirement(name = "api")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/create-from-cart")
    public ResponseEntity<Orders> createOrderFromCart() {
        try {
            Orders order = orderService.createOrderFromCart();
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/payment-url/{orderId}")
    public ResponseEntity<String> createPaymentUrl(@PathVariable long orderId) {
        try {
            String paymentUrl = orderService.createUrl(orderId);
            return ResponseEntity.ok(paymentUrl);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/update-post-count/{orderId}")
    public ResponseEntity<Void> updatePostCount(@PathVariable long orderId) {
        try {
            orderService.updatePostCountAfterPayment(orderId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}

