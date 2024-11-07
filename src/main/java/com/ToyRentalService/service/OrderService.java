package com.ToyRentalService.service;
import com.ToyRentalService.Dtos.Response.OrderHistoryResponse;
import com.ToyRentalService.entity.*;
import com.ToyRentalService.enums.OrderStatus;
import com.ToyRentalService.enums.OrderType;
import com.ToyRentalService.enums.PaymentStatus;
import com.ToyRentalService.exception.exceptions.NotFoundException;
import com.ToyRentalService.repository.*;
import jakarta.persistence.criteria.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

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

    @Autowired
    private EmailService emailService;

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
        Account seller = order.getOrderItems().get(0).getToy().getCustomer();
        Account buyer = authenticationService.getCurrentAccount();
        String buyEmail= buyer.getEmail();
        String sellEmail= seller.getEmail();
        Map<String, Object> buyerTemplateModel = new HashMap<>();
        buyerTemplateModel.put("customerName", order.getCustomer().getUsername());
        buyerTemplateModel.put("orderId", order.getId());
        buyerTemplateModel.put("orderDate", order.getCreateAt());
        buyerTemplateModel.put("orderType", "Purchase"); // hoặc "Rent"
        buyerTemplateModel.put("productName", order.getOrderItems().get(0).getToy().getToyName());
        buyerTemplateModel.put("quantity", order.getOrderItems().get(0).getQuantity());
        buyerTemplateModel.put("price", order.getOrderItems().get(0).getToy().getPrice());
        buyerTemplateModel.put("totalAmount", order.getTotalPrice());

        Map<String, Object> sellerTemplateModel = new HashMap<>();
        sellerTemplateModel.put("customerName", buyer.getUsername());
        sellerTemplateModel.put("orderId", order.getId());
        sellerTemplateModel.put("orderDate", order.getCreateAt());
        sellerTemplateModel.put("orderType", "Purchase"); // hoặc "Rent"
        sellerTemplateModel.put("productName", order.getOrderItems().get(0).getToy().getToyName());
        sellerTemplateModel.put("quantity", order.getOrderItems().get(0).getQuantity());
        sellerTemplateModel.put("price", order.getOrderItems().get(0).getToy().getPrice());
        sellerTemplateModel.put("totalAmount", order.getTotalPrice());

        // Gọi service gửi email
        emailService.sendOrderEmails(buyEmail, sellEmail, "Order Confirmation", "order-template", buyerTemplateModel, sellerTemplateModel);
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
//    public List<OrderHistoryResponse> getOrderItemHistoryForCurrentUser() {
//        Account currentUser = authenticationService.getCurrentAccount();
//        List<OrderItem> orderItems = orderItemRepository.findByOrdersCustomer(currentUser);
//        List<OrderHistoryResponse> orderHistoryResponse = new ArrayList<>();
//
//        for (OrderItem orderItem : orderItems) {
//            Orders order = orderItem.getOrders();
//            OrderStatus status = order.getStatus();
//
//            if (status == OrderStatus.COMPLETED) {
//                OrderHistoryResponse historyResponse = OrderHistoryResponse.builder()
//                        .orderId(order.getId())
//                        .orderDate(order.getCreateAt())
//                        .toys(Collections.singletonList(orderItem.getToy()))
//                        .build();
//                orderHistoryResponse.add(historyResponse);
//            }
//        }
//
//        return orderHistoryResponse;
//    }
//public List<OrderHistoryResponse> getOrderItemHistoryForCurrentUser() {
//    Account currentUser = authenticationService.getCurrentAccount();
//    List<OrderItem> orderItems = orderItemRepository.findByOrdersCustomer(currentUser);
//    Map<Long, OrderHistoryResponse> orderHistoryMap = new HashMap<>();
//
//    for (OrderItem orderItem : orderItems) {
//        Orders order = orderItem.getOrders();
//        OrderStatus status = order.getStatus();
//
//        if (status == OrderStatus.COMPLETED) {
//            long orderId = order.getId();
//            double itemPrice = orderItem.getPrice(); // Lấy giá của từng OrderItem
//            Toy toy = orderItem.getToy();
//
//            if (orderHistoryMap.containsKey(orderId)) {
//                OrderHistoryResponse existingResponse = orderHistoryMap.get(orderId);
//                existingResponse.getToys().add(toy);
//                existingResponse.setTotalPrice(existingResponse.getTotalPrice() + itemPrice);
//            } else {
//                OrderHistoryResponse historyResponse = OrderHistoryResponse.builder()
//                        .orderId(orderId)
//                        .orderDate(order.getCreateAt())
//                        .toys(new ArrayList<>(Collections.singletonList(toy)))
//                        .totalPrice(itemPrice)
//                        .build();
//                orderHistoryMap.put(orderId, historyResponse);
//            }
//        }
//    }
//
//    return new ArrayList<>(orderHistoryMap.values());
//}
public List<OrderHistoryResponse> getOrderHistoryForCurrentUser() {
    Account currentUser = authenticationService.getCurrentAccount();
    List<Payment> payments = paymentRepository.findByOrder_Customer_IdOrOrderRent_Customer_Id(
            currentUser.getId(), currentUser.getId()
    );
    List<OrderHistoryResponse> orderHistoryResponse = new ArrayList<>();

    for (Payment payment : payments) {
        if (payment.getPaymentStatus() == PaymentStatus.COMPLETED) {
            Long orderId = payment.getOrder() != null ? payment.getOrder().getId() : null;
            Long orderRentId = payment.getOrderRent() != null ? payment.getOrderRent().getId() : null;
            Long finalOrderId = orderId != null ? orderId : orderRentId;

            // Lấy thông tin toys từ Order hoặc OrderRent
            List<Toy> toys = getToysFromOrderOrOrderRent(payment);

            // Tạo response với thông tin cần thiết
            OrderHistoryResponse historyResponse = OrderHistoryResponse.builder()
                    .orderId(finalOrderId)
                    .orderDate(payment.getCreateAt())
                    .totalPrice(payment.getPrice())
                    .orderType(payment.getOrderType())
                    .toys(toys)  // Gán thông tin toys vào response
                    .build();

            orderHistoryResponse.add(historyResponse);
        }
    }

    return orderHistoryResponse;
}

    private List<Toy> getToysFromOrderOrOrderRent(Payment payment) {
        if (payment.getOrder() != null) {
            // Lấy toys từ Order nếu là đơn mua
            return payment.getOrder().getOrderItems().stream()
                    .map(OrderItem::getToy)
                    .collect(Collectors.toList());
        } else if (payment.getOrderRent() != null) {
            return payment.getOrderRent().getOrderRentItems().stream()
                    .map(OrderRentItem::getToy)
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
//    private List<Toy> getToysFromOrderOrOrderRent(Payment payment) {
//        List<Toy> toys = new ArrayList<>();
//        if (payment.getOrder() != null) {
//            toys = payment.getOrder().getOrderItems().stream()
//                    .map(OrderItem::getToy)
//                    .collect(Collectors.toList());
//            System.out.println("Toys from Order: " + toys);
//        } else if (payment.getOrderRent() != null) {
//            toys = payment.getOrderRent().getOrderRentItems().stream()
//                    .map(OrderRentItem::getToy)
//                    .collect(Collectors.toList());
//            System.out.println("Toys from OrderRent: " + toys);
//        }
//        return toys;
//    }


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

