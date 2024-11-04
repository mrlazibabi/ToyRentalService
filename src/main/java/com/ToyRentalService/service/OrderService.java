package com.ToyRentalService.service;
import com.ToyRentalService.Dtos.Request.OrderRequest.OrderPostTicketItemRequest;
import com.ToyRentalService.Dtos.Request.OrderRequest.OrderPostTicketRequest;
import com.ToyRentalService.Dtos.Response.OrderHistoryResponse;
import com.ToyRentalService.entity.*;
import com.ToyRentalService.enums.OrderStatus;
import com.ToyRentalService.enums.OrderType;
import com.ToyRentalService.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    private OrderHistoryRepository orderHistoryRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PostRepository postRepository;

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
        orderItem.setPost(cartItem.getPost());
        orderItem.setOrders(order);
        orderItem.setDayToRent(cartItem.getDayToRent());
        orderItem.setType(cartItem.getType());
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

    //addOrderHistory(savedOrder, OrderStatus.CREATED, "Order created for purchasing toys.");
    String paymentUrl = createUrl(savedOrder.getId());

    return paymentUrl;
}
    public void updateOrderStatusAfterPayment(long orderId, OrderStatus status) throws Exception {
        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new Exception("Order not found"));

        order.setStatus(status);
        orderRepository.save(order);

        String description = status == OrderStatus.COMPLETED ? "Payment completed successfully." : "Payment failed or canceled.";
        addOrderHistory(order, status, description);
    }
        public Orders createOrderPostTicket(OrderPostTicketRequest orderPostTicketRequest){
        Account customer = authenticationService.getCurrentAccount();
        Orders orders = new Orders();
        List<OrderItem> orderItems= new ArrayList<>();
        double total = 0;

        orders.setCustomer(customer);
        orders.setCreateAt(new Date());
        double postTicketPrice = 10000;
        for (OrderPostTicketItemRequest orderPostTicketItemRequest : orderPostTicketRequest.getItem()){
            OrderItem orderItem = new OrderItem();
            orderItem.setQuantity(orderPostTicketItemRequest.getQuantity());

            orderItem.setOrders(orders);

            orderItems.add(orderItem);
            total += postTicketPrice * orderPostTicketItemRequest.getQuantity();
        }

        orders.setOrderItems(orderItems);
        orders.setTotalPrice(total);
        orders.setType(OrderType.BUYPOST);
        return orderRepository.save(orders);
    }
    public void updateStockAfterPayment(long orderId) throws Exception {
        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new Exception("Order not found"));

        if (order.getStatus() == OrderStatus.COMPLETED && order.getType() != OrderType.BUYPOST) {
            for (OrderItem item : order.getOrderItems()) {
                Post post = item.getPost();
                if (post != null) {
                    post.decrementQuantity(item.getQuantity());
                    postRepository.save(post);
                } else {
                    throw new Exception("Post not found for order item.");
                }
            }
        }
    }
    public void addOrderHistory(Orders order, OrderStatus status, String description) {
        OrderHistory orderHistory = OrderHistory.builder()
                .order(order)
                .orderDate(new Date())
                .description(description)
                .status(status)
                .build();

        orderHistoryRepository.save(orderHistory);
    }

    public List<OrderHistory> getOrderHistoryByType(OrderType type) {
        return orderHistoryRepository.findByOrderType(type);
    }

    public List<OrderHistoryResponse> getOrderHistoryForCurrentUser() {
        Account currentUser = authenticationService.getCurrentAccount();
        List<OrderHistory> orderHistory = orderHistoryRepository.findByOrderCustomer(currentUser);
        List<Post> posts = new ArrayList<>();
        List<OrderHistoryResponse> orderHistoryResponse = new ArrayList<>();
        for (OrderHistory orderHistoryItem : orderHistory){
            for(OrderItem orderItem : orderHistoryItem.getOrder().getOrderItems()){
                posts.add(orderItem.getPost());
            }
            OrderHistoryResponse  orderHistoryResponse1 = OrderHistoryResponse.builder()
                    .orderType(orderHistoryItem.getOrder().getType())
                    .orderId(orderHistoryItem.getId())
                    .orderDate(orderHistoryItem.getOrderDate())
                    .posts(posts)
                    .build();
            if (orderHistoryItem.getStatus() == OrderStatus.COMPLETED) {
                orderHistoryResponse.add(orderHistoryResponse1);
            }
        }

        return orderHistoryResponse;
    }

    public String createUrl(long orderId) throws Exception {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        LocalDateTime createDate = LocalDateTime.now();
        String formattedCreateDate = createDate.format(formatter);

        Orders orders = orderRepository.findById(orderId)
                .orElseThrow(() -> new Exception("Order not found"));

        double money = orders.getTotalPrice() * 100;
        String amount = String.valueOf((int) money);
        String tmnCode = "P5CWZRAS";
        String secretKey = "74FW426Y4BRBGGIZ9HCR40EGGFXJ70IV";
        String vnpUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
        String returnUrl = "http://localhost:5173/success?orderID=" + orders.getId();
        String currCode = "VND";
        Map<String, String> vnpParams = new TreeMap<>();
        vnpParams.put("vnp_Version", "2.1.0");
        vnpParams.put("vnp_Command", "pay");
        vnpParams.put("vnp_TmnCode", tmnCode);
        vnpParams.put("vnp_Locale", "vn");
        vnpParams.put("vnp_CurrCode", currCode);
        vnpParams.put("vnp_TxnRef", String.valueOf(orders.getId())); // Sử dụng orderId làm TxnRef
        vnpParams.put("vnp_OrderInfo", "Thanh toán cho mã GD: " + orders.getId());
        vnpParams.put("vnp_OrderType", "other");
        vnpParams.put("vnp_Amount", amount);
        vnpParams.put("vnp_ReturnUrl", returnUrl);
        vnpParams.put("vnp_CreateDate", formattedCreateDate);
        vnpParams.put("vnp_IpAddr", "128.199.178.23");

        StringBuilder signDataBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : vnpParams.entrySet()) {
            signDataBuilder.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8.toString()));
            signDataBuilder.append("=");
            signDataBuilder.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8.toString()));
            signDataBuilder.append("&");
        }
        signDataBuilder.deleteCharAt(signDataBuilder.length() - 1);

        String signData = signDataBuilder.toString();
        String signed = generateHMAC(secretKey, signData);

        vnpParams.put("vnp_SecureHash", signed);

        StringBuilder urlBuilder = new StringBuilder(vnpUrl);
        urlBuilder.append("?");
        for (Map.Entry<String, String> entry : vnpParams.entrySet()) {
            urlBuilder.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8.toString()));
            urlBuilder.append("=");
            urlBuilder.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8.toString()));
            urlBuilder.append("&");
        }
        urlBuilder.deleteCharAt(urlBuilder.length() - 1);

        return urlBuilder.toString();
    }

    public void updatePostCountAfterPayment(long orderId) throws Exception {
        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new Exception("Order not found"));

        if (order.getType() == OrderType.BUYPOST && order.getStatus() == OrderStatus.COMPLETED) {
            Account customer = order.getCustomer();
            int totalPostCount = order.getOrderItems().stream()
                    .mapToInt(OrderItem::getQuantity)
                    .sum();

            customer.setPostCount(customer.getPostCount() + totalPostCount);
            accountRepository.save(customer);
        }
    }
    public Orders createOrder(Orders order) {
        return orderRepository.save(order);
    }

    private String generateHMAC(String secretKey, String signData) throws Exception {
        Mac hmacSha512 = Mac.getInstance("HmacSHA512");
        SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
        hmacSha512.init(keySpec);
        byte[] hmacBytes = hmacSha512.doFinal(signData.getBytes(StandardCharsets.UTF_8));

        StringBuilder result = new StringBuilder();
        for (byte b : hmacBytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }
}

