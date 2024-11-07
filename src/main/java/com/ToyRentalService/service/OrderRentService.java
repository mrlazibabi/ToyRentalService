package com.ToyRentalService.service;
import com.ToyRentalService.entity.*;
import com.ToyRentalService.enums.OrderRentStatus;
import com.ToyRentalService.enums.OrderStatus;
import com.ToyRentalService.enums.OrderType;
import com.ToyRentalService.enums.PaymentStatus;
import com.ToyRentalService.exception.exceptions.NotFoundException;
import com.ToyRentalService.repository.*;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class OrderRentService {
    @Autowired
    private OrderRentRepository orderRentRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private ToyRepository toyRepository;

    @Autowired
    private VNPayPaymentService vnpayPaymentService;

    @Autowired
    private OrderRentItemRepository orderRentItemRepository;
    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private EmailService emailService;

//    public String createOrderRent(Long toyId, int quantity, int daysToRent) throws Exception {
//        Toy toy = toyRepository.findById(toyId).orElseThrow(() -> new NotFoundException("Toy not found"));
//
//        Account customer = authenticationService.getCurrentAccount();
//        if (customer == null) {
//            throw new NotFoundException("User not logged in");
//        }
//
//        if (toy.getQuantity() < quantity) {
//            throw new Exception("Insufficient stock");
//        }
//
//        OrderRent orderRent = new OrderRent();
//        orderRent.setCustomer(customer);
//        orderRent.setTotalPrice(toy.getPriceByDay()* quantity * daysToRent+toy.getDepositFee());
//        orderRent.setStatus(OrderRentStatus.PENDING);
//        orderRent.setDueDate(calculateDueDate(daysToRent));
//
//        toy.decrementQuantity(quantity);
//        toyRepository.save(toy);
//        OrderRent savedOrderRent = orderRentRepository.save(orderRent);
//        OrderRentItem orderRentItem = new OrderRentItem();
//        orderRentItem.setOrderRent(savedOrderRent);
//        orderRentItem.setToy(toy);
//        orderRentItem.setPrice(toy.getPriceByDay());
//        orderRentItem.setQuantity(quantity);
//        orderRentItem.setDaysToRent(daysToRent);
//        orderRentItemRepository.save(orderRentItem);
////        try {
////            emailService.sendOrderRentConfirmationEmail(savedOrderRent, customer.getEmail());
////        } catch (MessagingException e) {
////            System.err.println("Failed to send order rent confirmation email: " + e.getMessage());
////        }
//        Account seller = orderRent.getOrderRentItems().get(0).getToy().getCustomer();
//        Account buyer = authenticationService.getCurrentAccount();
//        String buyEmail= buyer.getEmail();
//        String sellEmail= seller.getEmail();
//        Map<String, Object> buyerTemplateModel = new HashMap<>();
//        buyerTemplateModel.put("customerName", orderRent.getCustomer().getUsername());
//        buyerTemplateModel.put("orderId", orderRent.getId());
//        buyerTemplateModel.put("orderDate", orderRent.getCreatedAt());
//        buyerTemplateModel.put("orderType", "RentToy");
//        buyerTemplateModel.put("productName", orderRent.getOrderRentItems().get(0).getToy().getToyName());
//        buyerTemplateModel.put("rentalDays", orderRent.getRentalDays());
//        buyerTemplateModel.put("quantity", orderRent.getOrderRentItems().get(0).getQuantity());
//        buyerTemplateModel.put("price", orderRent.getOrderRentItems().get(0).getToy().getPrice());
//        buyerTemplateModel.put("DepositFee", orderRent.getOrderRentItems().get(0).getToy().getDepositFee());
//        buyerTemplateModel.put("totalAmount", orderRent.getTotalPrice());
//
//        Map<String, Object> sellerTemplateModel = new HashMap<>();
//        sellerTemplateModel.put("customerName", buyer.getUsername());
//        sellerTemplateModel.put("orderId", orderRent.getId());
//        sellerTemplateModel.put("orderDate", orderRent.getCreatedAt());
//        sellerTemplateModel.put("orderType", "RentToy");
//        sellerTemplateModel.put("productName", orderRent.getOrderRentItems().get(0).getToy().getToyName());
//        sellerTemplateModel.put("rentalDays", orderRent.getRentalDays());
//        sellerTemplateModel.put("quantity", orderRent.getOrderRentItems().get(0).getQuantity());
//        sellerTemplateModel.put("price", orderRent.getOrderRentItems().get(0).getToy().getPrice());
//        sellerTemplateModel.put("DepositFee", orderRent.getOrderRentItems().get(0).getToy().getDepositFee());
//        sellerTemplateModel.put("totalAmount", orderRent.getTotalPrice());
//        emailService.sendOrderEmails(buyEmail, sellEmail, "Order Confirmation", "orderRent-template", buyerTemplateModel, sellerTemplateModel);
//        return initiateOrderRentPayment(savedOrderRent.getId());
//    }
public String createOrderRent(Long toyId, int quantity, int daysToRent) throws Exception {
    Toy toy = toyRepository.findById(toyId).orElseThrow(() -> new NotFoundException("Toy not found"));
    Account customer = authenticationService.getCurrentAccount();

    if (customer == null) {
        throw new NotFoundException("User not logged in");
    }

    if (toy.getQuantity() < quantity) {
        throw new Exception("Insufficient stock");
    }

    OrderRent orderRent = new OrderRent();
    orderRent.setCustomer(customer);
    orderRent.setTotalPrice(toy.getPriceByDay() * quantity * daysToRent + toy.getDepositFee());
    orderRent.setStatus(OrderRentStatus.PENDING);
    orderRent.setDueDate(calculateDueDate(daysToRent));

    toy.decrementQuantity(quantity);
    toyRepository.save(toy);

    OrderRentItem orderRentItem = new OrderRentItem();
    orderRentItem.setOrderRent(orderRent);
    orderRentItem.setToy(toy);
    orderRentItem.setPrice(toy.getPriceByDay());
    orderRentItem.setQuantity(quantity);
    orderRentItem.setDaysToRent(daysToRent);

    if (orderRent.getOrderRentItems() == null) {
        orderRent.setOrderRentItems(new ArrayList<>());
    }
    orderRent.getOrderRentItems().add(orderRentItem);

    OrderRent savedOrderRent = orderRentRepository.save(orderRent);
    orderRentItemRepository.save(orderRentItem);

    return initiateOrderRentPayment(savedOrderRent.getId());
}
    public String initiateOrderRentPayment(Long orderRentId) throws Exception {
        OrderRent orderRent = orderRentRepository.findById(orderRentId)
                .orElseThrow(() -> new Exception("OrderRent not found"));

        String returnUrl = "http://localhost:5173/success?orderRentID=" + orderRent.getId();
        String orderInfo = "Thanh toán đơn thuê mã: " + orderRent.getId();
        int amount = (int) (orderRent.getTotalPrice() );

        return vnpayPaymentService.createPaymentUrl(amount, orderInfo, returnUrl);
    }

    public void updateOrderRentStatusAfterPayment(long orderRentId, OrderRentStatus status) throws Exception {
        OrderRent orderRent = orderRentRepository.findById(orderRentId)
                .orElseThrow(() -> new Exception("OrderRent not found"));

        orderRent.setStatus(status);
        orderRentRepository.save(orderRent);

        PaymentStatus paymentStatus = (status == OrderRentStatus.DELIVERING) ? PaymentStatus.COMPLETED : PaymentStatus.FAILED;
        savePaymentRecord(orderRent, orderRent.getTotalPrice(), paymentStatus);

        // Xử lý gửi email sau khi cập nhật trạng thái đơn hàng
        Account seller = orderRent.getOrderRentItems().get(0).getToy().getCustomer();
        Account buyer = authenticationService.getCurrentAccount();
        String buyEmail = buyer.getEmail();
        String sellEmail = seller.getEmail();

        Map<String, Object> buyerTemplateModel = new HashMap<>();
        buyerTemplateModel.put("customerName", orderRent.getCustomer().getUsername());
        buyerTemplateModel.put("orderId", orderRent.getId());
        buyerTemplateModel.put("orderDate", orderRent.getCreatedAt());
        buyerTemplateModel.put("orderType", "RentToy");
        buyerTemplateModel.put("productName", orderRent.getOrderRentItems().get(0).getToy().getToyName());
        buyerTemplateModel.put("rentalDays", orderRent.getOrderRentItems().get(0).getDaysToRent());
        buyerTemplateModel.put("quantity", orderRent.getOrderRentItems().get(0).getQuantity());
        buyerTemplateModel.put("price", orderRent.getOrderRentItems().get(0).getPrice());
        buyerTemplateModel.put("DepositFee", orderRent.getOrderRentItems().get(0).getToy().getDepositFee());
        buyerTemplateModel.put("totalAmount", orderRent.getTotalPrice());

        Map<String, Object> sellerTemplateModel = new HashMap<>(buyerTemplateModel);
        sellerTemplateModel.put("customerName", buyer.getUsername());

        // Gửi email cho cả người mua và người bán
        emailService.sendOrderEmails(buyEmail, sellEmail, "Order Confirmation", "orderRent-template", buyerTemplateModel, sellerTemplateModel);
    }


    private void savePaymentRecord(OrderRent orderRent, double amount, PaymentStatus status) {
        Payment payment = new Payment();
        payment.setOrderRent(orderRent);
        payment.setCreateAt(new Date());
        payment.setPrice((float) amount);
        payment.setPaymentStatus(status);
        payment.setOrderType(OrderType.RENTTOY);
        payment.setIsDeposit(true);
        paymentRepository.save(payment);
    }
    public void markOrderAsDelivered(Long orderId) throws Exception {
        OrderRent orderRent = orderRentRepository.findById(orderId)
                .orElseThrow(() -> new Exception("Order not found"));
        orderRent.setStatus(OrderRentStatus.DELIVERED);
        orderRent.setDeliveredAt(new Date());
        orderRentRepository.save(orderRent);
    }

    public void updateOrderStatusToOverdue() {
        List<OrderRent> orders = orderRentRepository.findAll();
        Date currentDate = new Date();

        for (OrderRent order : orders) {
            if (order.getStatus() == OrderRentStatus.DELIVERED && currentDate.after(order.getDueDate())) {
                order.setStatus(OrderRentStatus.OVERDUE);
                double lateFee = calculateLateFee(order);
                order.setLateFee(lateFee);
                orderRentRepository.save(order);
            }
        }
    }

    private double calculateLateFee(OrderRent order) {
        long daysLate = (new Date().getTime() - order.getDueDate().getTime()) / (1000 * 60 * 60 * 24);
        return daysLate * 5.0; //
    }

    public void returnOrder(Long orderId) throws NotFoundException {
        OrderRent order = orderRentRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found"));

        if (new Date().after(order.getDueDate())) {
            order.setStatus(OrderRentStatus.RETURNED_LATE);
        } else {
            order.setStatus(OrderRentStatus.RETURNED);
        }

        order.setReturnDate(new Date());
        orderRentRepository.save(order);
    }
    private Date calculateDueDate(int daysToRent) {
        Date dueDate = new Date();
        dueDate.setTime(dueDate.getTime() + (daysToRent * 24 * 60 * 60 * 1000));
        return dueDate;
    }
}
