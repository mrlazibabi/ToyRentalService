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
    private CartItemRepository cartItemRepository;  // Thêm CartItemRepository để có thể thao tác với CartItem

    @Autowired
    private AuthenticationService authenticationService;

    public void addItemToCart(long postId, int quantity, OrderType type, int dayToRent) throws Exception {
        // Lấy thông tin post từ postId
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new Exception("Post not found"));

        // Lấy thông tin tài khoản hiện tại
        Account customer = authenticationService.getCurrentAccount();

        // Tìm kiếm cart của khách hàng
        Cart cart = cartRepository.findByCustomer(customer)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setCustomer(customer);
                    newCart.setCartItems(new ArrayList<>());
                    return cartRepository.save(newCart);  // Lưu Cart mới vào cơ sở dữ liệu
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
                existingItem.setDayToRent(dayToRent);  // Cập nhật số ngày thuê nếu có
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
            newItem.setCart(cart);  // Liên kết Cart với CartItem

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

        // Cập nhật tổng số lượng và tổng giá trong giỏ hàng
        int totalQuantity = cart.getCartItems().stream().mapToInt(CartItem::getQuantity).sum();
        double totalPrice = cart.getCartItems().stream().mapToDouble(CartItem::getPrice).sum();

        cart.setTotalPrice(totalPrice);
        cartRepository.save(cart);  // Lưu lại giỏ hàng sau khi cập nhật
    }

    public Cart getCart() {
        // Lấy thông tin tài khoản hiện tại
        Account customer = authenticationService.getCurrentAccount();
        // Trả về cart của tài khoản
        return cartRepository.findByCustomer(customer)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
    }
    public void clearCart() {
        Account customer = authenticationService.getCurrentAccount();
        Cart cart = cartRepository.findByCustomer(customer)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        if (!cart.getCartItems().isEmpty()) {
            // Xóa tất cả các CartItem
            cartItemRepository.deleteAll(cart.getCartItems());
            cart.getCartItems().clear();

            // Đặt lại giá trị TotalPrice về 0 và lưu lại giỏ hàng
            cart.setTotalPrice(0.0);
            cartRepository.save(cart);
        }
    }

}

