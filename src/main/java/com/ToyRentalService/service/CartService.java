package com.ToyRentalService.service;

import com.ToyRentalService.entity.*;
import com.ToyRentalService.enums.OrderType;
import com.ToyRentalService.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class CartService {

    @Autowired
    private ToyRepository toyRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private AuthenticationService authenticationService;

    public void addItemToCart(long postId, int quantity) throws Exception {
        Toy toy = toyRepository.findById(postId)
                .orElseThrow(() -> new Exception("Toy not found"));

        Account customer = authenticationService.getCurrentAccount();

        Cart cart = cartRepository.findByCustomer(customer)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setCustomer(customer);
                    newCart.setCartItems(new ArrayList<>());
                    return cartRepository.save(newCart);
                });

        Optional<CartItem> existingItemOpt = cart.getCartItems().stream()
                .filter(item -> item.getToy().getId() == postId)
                .findFirst();

        if (existingItemOpt.isPresent()) {
            CartItem existingItem = existingItemOpt.get();
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            existingItem.setPrice(toy.getPrice() * existingItem.getQuantity());
            cartItemRepository.save(existingItem);
        } else {
            CartItem newItem = new CartItem();
            newItem.setToy(toy);
            newItem.setQuantity(quantity);
            newItem.setCart(cart);
            newItem.setPrice(toy.getPrice() * quantity);
            cart.getCartItems().add(newItem);
            cartItemRepository.save(newItem);
        }

        double totalPrice = cart.getCartItems().stream().mapToDouble(CartItem::getPrice).sum();
        cart.setTotalPrice(totalPrice);
        cartRepository.save(cart);
    }

    public Cart getCart() {
        Account customer = authenticationService.getCurrentAccount();
        return cartRepository.findByCustomer(customer)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
    }
    public void clearCart() {
        Account customer = authenticationService.getCurrentAccount();
        Cart cart = cartRepository.findByCustomer(customer)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        if (!cart.getCartItems().isEmpty()) {
            cartItemRepository.deleteAll(cart.getCartItems());
            cart.getCartItems().clear();
            cart.setTotalPrice(0.0);
            cartRepository.save(cart);
        }
    }
    public void removeItemFromCart(long postId) throws Exception {
        Account customer = authenticationService.getCurrentAccount();
        Cart cart = cartRepository.findByCustomer(customer)
                .orElseThrow(() -> new Exception("Cart not found"));

        CartItem existingItem = cart.getCartItems().stream()
                .filter(item -> item.getToy().getId() == postId)
                .findFirst()
                .orElseThrow(() -> new Exception("Item not found in cart"));

        cartItemRepository.delete(existingItem);
        cart.getCartItems().remove(existingItem);
        cart.setTotalPrice(cart.getCartItems().stream().mapToDouble(CartItem::getPrice).sum());
        cartRepository.save(cart);
    }
    public void updateItemQuantity(long postId, int quantity) throws Exception {
        Account customer = authenticationService.getCurrentAccount();
        Cart cart = cartRepository.findByCustomer(customer)
                .orElseThrow(() -> new Exception("Cart not found"));

        CartItem existingItem = cart.getCartItems().stream()
                .filter(item -> item.getToy().getId() == postId)
                .findFirst()
                .orElseThrow(() -> new Exception("Item not found in cart"));

        existingItem.setQuantity(quantity);
        existingItem.setPrice(existingItem.getToy().getPrice() * quantity);
        cartItemRepository.save(existingItem);

        cart.setTotalPrice(cart.getCartItems().stream().mapToDouble(CartItem::getPrice).sum());
        cartRepository.save(cart);
    }


}

