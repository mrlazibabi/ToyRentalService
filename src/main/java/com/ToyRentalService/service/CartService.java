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
    private PostRepository postRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private AuthenticationService authenticationService;

    public void addItemToCart(long postId, int quantity, OrderType type, int dayToRent) throws Exception {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new Exception("Post not found"));

        Account customer = authenticationService.getCurrentAccount();

        Cart cart = cartRepository.findByCustomer(customer)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setCustomer(customer);
                    newCart.setCartItems(new ArrayList<>());
                    return cartRepository.save(newCart);
                });

        // Kiểm tra xem item đã có trong cart chưa
        Optional<CartItem> existingItemOpt = cart.getCartItems().stream()
                .filter(item -> item.getPost().getId() == postId && item.getType() == type)
                .findFirst();

        if (existingItemOpt.isPresent()) {
            // Nếu đã tồn tại, cập nhật số lượng và giá của CartItem
            CartItem existingItem = existingItemOpt.get();
            existingItem.setQuantity(existingItem.getQuantity() + quantity);

            if (type == OrderType.RENTTOY) {
                existingItem.setDayToRent(dayToRent);
                existingItem.setPrice(post.getPriceByDay() * existingItem.getDayToRent() + post.getDepositFee());
            } else if (type == OrderType.BUYTOY) {
                existingItem.setPrice(post.getPrice() * existingItem.getQuantity());
            }

            cartItemRepository.save(existingItem);
        } else {
            // Thêm mới CartItem
            CartItem newItem = new CartItem();
            newItem.setPost(post);
            newItem.setQuantity(quantity);
            newItem.setType(type);
            newItem.setCart(cart);
            if (type == OrderType.RENTTOY) {
                newItem.setDayToRent(dayToRent);
                newItem.setPrice(post.getPriceByDay() * dayToRent + post.getDepositFee());
            } else if (type == OrderType.BUYTOY) {
                newItem.setPrice(post.getPrice() * quantity);
            } else if (type == OrderType.BUYPOST) {
                double postTicketPrice = 10000;
                newItem.setPrice(postTicketPrice * quantity);
            }
            cart.getCartItems().add(newItem);
            cartItemRepository.save(newItem);
        }
        int totalQuantity = cart.getCartItems().stream().mapToInt(CartItem::getQuantity).sum();
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
    public void removeItemFromCart(long postId, OrderType type) throws Exception {
        Account customer = authenticationService.getCurrentAccount();
        Cart cart = cartRepository.findByCustomer(customer)
                .orElseThrow(() -> new Exception("Cart not found"));

        CartItem existingItem = cart.getCartItems().stream()
                .filter(item -> item.getPost().getId() == postId && item.getType() == type)
                .findFirst()
                .orElseThrow(() -> new Exception("Item not found in cart"));

        cartItemRepository.delete(existingItem);
        cart.getCartItems().remove(existingItem);
        cart.setTotalPrice(cart.getCartItems().stream().mapToDouble(CartItem::getPrice).sum());
        cartRepository.save(cart);
    }
    public void updateItemQuantity(long postId, OrderType type, int quantity, int dayToRent) throws Exception {
        Account customer = authenticationService.getCurrentAccount();
        Cart cart = cartRepository.findByCustomer(customer)
                .orElseThrow(() -> new Exception("Cart not found"));

        CartItem existingItem = cart.getCartItems().stream()
                .filter(item -> item.getPost().getId() == postId && item.getType() == type)
                .findFirst()
                .orElseThrow(() -> new Exception("Item not found in cart"));

        existingItem.setQuantity(quantity);

        if (type == OrderType.RENTTOY) {
            existingItem.setDayToRent(dayToRent);
            existingItem.setPrice(existingItem.getPost().getPriceByDay() * dayToRent + existingItem.getPost().getDepositFee());
        } else if (type == OrderType.BUYTOY) {
            existingItem.setPrice(existingItem.getPost().getPrice() * quantity);
        }

        cartItemRepository.save(existingItem);
        cart.setTotalPrice(cart.getCartItems().stream().mapToDouble(CartItem::getPrice).sum());
        cartRepository.save(cart);
    }


}

