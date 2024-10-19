package com.ToyRentalService.service;

import com.ToyRentalService.Dtos.Request.OrderRequest.*;
import com.ToyRentalService.entity.Account;
import com.ToyRentalService.entity.Orders;
import com.ToyRentalService.entity.OrderItem;
import com.ToyRentalService.entity.Post;
import com.ToyRentalService.enums.OrderType;
import com.ToyRentalService.repository.OrderRepository;
import com.ToyRentalService.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class    OrderService {

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    PostRepository postRepository;

    @Autowired
    OrderRepository orderRepository;

    public Orders createOrderBuy(OrderBuyRequest orderBuyRequest){
        Account customer = authenticationService.getCurrentAccount();
        Orders orders = new Orders();
        List<OrderItem> orderItems= new ArrayList<>();
        double total = 0;

        orders.setCustomer(customer);
        orders.setCreateAt(new Date());

        for (OrderBuyItemRequest orderBuyItemRequest : orderBuyRequest.getItem()){
            Post post = postRepository.findPostById(orderBuyItemRequest.getPostId());
            OrderItem orderItem = new OrderItem();
            orderItem.setQuantity(orderBuyItemRequest.getQuantity());
            orderItem.setPrice(post.getPrice());
            orderItem.setOrders(orders);
            orderItem.setPost(post);
            orderItems.add(orderItem);
            total +=post.getPrice() * orderBuyItemRequest.getQuantity();
        }

        orders.setOrderItems(orderItems);
        orders.setTotalPrice(total);
        orders.setType(OrderType.BUYTOY);
        return orderRepository.save(orders);
    }


    public Orders createOrderRent(OrderRentRequest orderRentRequest){
        Account customer = authenticationService.getCurrentAccount();
        Orders orders = new Orders();
        List<OrderItem> orderItems= new ArrayList<>();
        double total = 0;

        orders.setCustomer(customer);
        orders.setCreateAt(new Date());

        for (OrderRentItemRequest orderRentItemRequest : orderRentRequest.getItem()){
            Post post = postRepository.findPostById(orderRentItemRequest.getPostId());
            OrderItem orderItem = new OrderItem();
            orderItem.setQuantity(orderRentItemRequest.getQuantity());
            orderItem.setDayToRent(orderRentItemRequest.getDayToRent());
            orderItem.setOrders(orders);
            orderItem.setPost(post);
            double price = post.getDepositFee() + post.getPriceByDay()*orderRentItemRequest.getDayToRent();
            orderItem.setPrice(price);

            orderItems.add(orderItem);
            total += orderItem.getPrice()* orderRentItemRequest.getQuantity();
        }

        orders.setOrderItems(orderItems);
        orders.setTotalPrice(total);
        orders.setType(OrderType.RENTTOY);
        return orderRepository.save(orders);
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

    public String createUrl(long orderId) throws Exception {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        LocalDateTime createDate = LocalDateTime.now();
        String formattedCreateDate = createDate.format(formatter);

        // Lấy thông tin đơn hàng từ orderId
        Orders orders = orderRepository.findById(orderId)
                .orElseThrow(() -> new Exception("Order not found"));

        double money = orders.getTotalPrice() * 100; // Chuyển đổi giá trị tiền tệ
        String amount = String.valueOf((int) money); // Chuyển nó về string để gửi qua VNPay

        // Thông tin cần thiết cho VNPay
        String tmnCode = "P5CWZRAS";
        String secretKey = "74FW426Y4BRBGGIZ9HCR40EGGFXJ70IV";
        String vnpUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
        String returnUrl = "https://www.facebook.com/" + orders.getId();
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

        // Tạo dữ liệu chữ ký
        StringBuilder signDataBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : vnpParams.entrySet()) {
            signDataBuilder.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8.toString()));
            signDataBuilder.append("=");
            signDataBuilder.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8.toString()));
            signDataBuilder.append("&");
        }
        signDataBuilder.deleteCharAt(signDataBuilder.length() - 1); // Xóa '&' cuối cùng

        String signData = signDataBuilder.toString();
        String signed = generateHMAC(secretKey, signData);

        vnpParams.put("vnp_SecureHash", signed);

        // Tạo URL cho VNPay
        StringBuilder urlBuilder = new StringBuilder(vnpUrl);
        urlBuilder.append("?");
        for (Map.Entry<String, String> entry : vnpParams.entrySet()) {
            urlBuilder.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8.toString()));
            urlBuilder.append("=");
            urlBuilder.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8.toString()));
            urlBuilder.append("&");
        }
        urlBuilder.deleteCharAt(urlBuilder.length() - 1); // Xóa '&' cuối cùng

        return urlBuilder.toString();
    }
    private String generateHMAC(String secretKey, String signData) throws NoSuchAlgorithmException, InvalidKeyException {
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
