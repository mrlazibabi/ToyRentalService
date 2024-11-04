package com.ToyRentalService.api;
import com.ToyRentalService.entity.Cart;
import com.ToyRentalService.entity.CartItem;
import com.ToyRentalService.enums.OrderType;
import com.ToyRentalService.service.CartService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin("*")
@SecurityRequirement(name = "api")
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("/add")
    public ResponseEntity<?> addItemToCart(@RequestParam long postId,
                                           @RequestParam int quantity,
                                           @RequestParam OrderType type,
                                           @RequestParam(required = false) Integer dayToRent) {

        try {
            cartService.addItemToCart(postId, quantity, type, dayToRent != null ? dayToRent : 0);
            return ResponseEntity.ok("Item added to cart successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    @PostMapping("/remove")
    public ResponseEntity<?> removeItemFromCart(@RequestParam long postId, @RequestParam OrderType type) {
        try {
            cartService.removeItemFromCart(postId, type);
            return ResponseEntity.ok("Item removed from cart successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    @PostMapping("/update")
    public ResponseEntity<?> updateItemQuantity(@RequestParam long postId,
                                                @RequestParam OrderType type,
                                                @RequestParam int quantity,
                                                @RequestParam(required = false) Integer dayToRent) {
        try {
            cartService.updateItemQuantity(postId, type, quantity, dayToRent != null ? dayToRent : 0);
            return ResponseEntity.ok("Item quantity updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    @GetMapping("/summary")
    public ResponseEntity<?> getCartSummary() {
        try {
            Cart cart = cartService.getCart();
            int totalQuantity = cart.getCartItems().stream().mapToInt(CartItem::getQuantity).sum();
            double totalPrice = cart.getTotalPrice();
            return ResponseEntity.ok(Map.of("totalQuantity", totalQuantity, "totalPrice", totalPrice));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    @GetMapping("/items")
    public ResponseEntity<?> getCartItems() {
        try {
            Cart cart = cartService.getCart();
            return ResponseEntity.ok(cart.getCartItems());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    @GetMapping
    public Cart getCart() {
        return cartService.getCart();
    }

    @PostMapping("/clear")
    public void clearCart() {
        cartService.clearCart();
    }
}
