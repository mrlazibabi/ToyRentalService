package com.ToyRentalService.api;
import com.ToyRentalService.entity.Cart;
import com.ToyRentalService.enums.OrderType;
import com.ToyRentalService.service.CartService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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




    @GetMapping
    public Cart getCart() {
        return cartService.getCart();
    }

    @PostMapping("/clear")
    public void clearCart() {
        cartService.clearCart();
    }
}
