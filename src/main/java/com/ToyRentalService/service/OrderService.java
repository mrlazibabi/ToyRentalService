package com.ToyRentalService.service;
import com.ToyRentalService.Dtos.Response.OrderHistoryResponse;
import com.ToyRentalService.entity.*;
import com.ToyRentalService.enums.OrderStatus;
import com.ToyRentalService.enums.OrderType;
import com.ToyRentalService.enums.PaymentStatus;
import com.ToyRentalService.exception.exceptions.NotFoundException;
import com.ToyRentalService.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class OrderService {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ToyRepository toyRepository;

    @Autowired
    private VNPayPaymentService vnpayPaymentService;

    @Autowired
    private PaymentRepository paymentRepository;

public String createOrderFromCart() throws Exception {
    Account customer = authenticationService.getCurrentAccount();
    Cart cart = cartRepository.findByCustomer(customer)
            .orElseThrow(() -> new Exception("Cart not found"));

    if (cart.getCartItems().isEmpty()) {
        throw new Exception("Cart is empty");
    }

    Orders order = new Orders();
    order.setCustomer(customer);
    order.setCreateAt(new Date());
    order.setStatus(OrderStatus.PENDING);

    List<OrderItem> orderItems = new ArrayList<>();
    double total = 0;

    for (CartItem cartItem : cart.getCartItems()) {
        OrderItem orderItem = new OrderItem();
        orderItem.setQuantity(cartItem.getQuantity());
        orderItem.setToy(cartItem.getToy());
        orderItem.setOrders(order);
        orderItem.setPrice(cartItem.getPrice());
        orderItems.add(orderItem);

        total += cartItem.getPrice();
    }

    order.setOrderItems(orderItems);
    order.setTotalPrice(total);
    order.setType(cart.getCartItems().get(0).getType());

    Orders savedOrder = orderRepository.save(order);

    cart.getCartItems().clear();
    cart.setTotalPrice(0);
    cartRepository.save(cart);
    String paymentUrl = initiateOrderBuyPayment(savedOrder.getId());
    return paymentUrl;
}
public void updateOrderStatusAfterPayment(long orderId, OrderStatus status) throws Exception {
    Orders order = orderRepository.findById(orderId)
            .orElseThrow(() -> new Exception("Order not found"));
    order.setStatus(status);
    orderRepository.save(order);
    String description = status == OrderStatus.COMPLETED ? "Payment completed successfully." : "Payment failed or canceled.";
    if (status == OrderStatus.COMPLETED) {
        savePaymentRecord(order, order.getTotalPrice(), PaymentStatus.COMPLETED, false);
    } else {
        savePaymentRecord(order, order.getTotalPrice(), PaymentStatus.FAILED, false);
    }
}

    private void savePaymentRecord(Orders order, double amount, PaymentStatus status, boolean isDeposit) {
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setCreateAt(new Date());
        payment.setPrice((float) amount);
        payment.setPaymentStatus(status);
        payment.setIsDeposit(isDeposit);
        payment.setOrderType(OrderType.BUYTOY);
        paymentRepository.save(payment);
    }
    public void updateStockAfterPayment(long orderId) throws Exception {
        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new Exception("Order not found"));

        if (order.getStatus() == OrderStatus.COMPLETED) {
            for (OrderItem item : order.getOrderItems()) {
                Toy toy = item.getToy();
                if (toy != null) {
                    toy.decrementQuantity(item.getQuantity());
                    toyRepository.save(toy);
                } else {
                    throw new Exception("Toy not found for order item.");
                }
            }
        }
    }
    public List<OrderHistoryResponse> getOrderItemHistoryForCurrentUser() {
        Account currentUser = authenticationService.getCurrentAccount();
        List<OrderItem> orderItems = orderItemRepository.findByOrdersCustomer(currentUser);
        List<OrderHistoryResponse> orderHistoryResponse = new ArrayList<>();

        for (OrderItem orderItem : orderItems) {
            Orders order = orderItem.getOrders();
            OrderStatus status = order.getStatus();

            if (status == OrderStatus.COMPLETED) {
                OrderHistoryResponse historyResponse = OrderHistoryResponse.builder()
                        .orderId(order.getId())
                        .orderDate(order.getCreateAt())
                        .toys(Collections.singletonList(orderItem.getToy()))
                        .build();
                orderHistoryResponse.add(historyResponse);
            }
        }

        return orderHistoryResponse;
    }
    public String initiateOrderBuyPayment(Long orderId) throws Exception {
        Account customer = authenticationService.getCurrentAccount();
        if (customer == null) {
            throw new NotFoundException("User not logged in");
        }

        Orders orders = orderRepository.findById(orderId)
                .orElseThrow(() -> new Exception("Order not found"));
        String returnUrl = "http://localhost:5173/success?orderID=" + orders.getId();
        String orderInfo = "Thanh toán cho mã GD: " + orders.getId();
        double money = orders.getTotalPrice() ;
        int amount = (int) money;
        return vnpayPaymentService.createPaymentUrl(amount, orderInfo, returnUrl);
    }
}

