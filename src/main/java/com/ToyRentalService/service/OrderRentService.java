package com.ToyRentalService.service;
import com.ToyRentalService.entity.*;
import com.ToyRentalService.enums.OrderRentStatus;
import com.ToyRentalService.enums.OrderStatus;
import com.ToyRentalService.enums.OrderType;
import com.ToyRentalService.enums.PaymentStatus;
import com.ToyRentalService.exception.exceptions.NotFoundException;
import com.ToyRentalService.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;

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
        orderRent.setTotalPrice(toy.getPriceByDay()* quantity * daysToRent+toy.getDepositFee());
        orderRent.setStatus(OrderRentStatus.PENDING);
        orderRent.setDueDate(calculateDueDate(daysToRent));

        toy.decrementQuantity(quantity);
        toyRepository.save(toy);
        OrderRent savedOrderRent = orderRentRepository.save(orderRent);

        return initiateOrderRentPayment(savedOrderRent.getId());
    }

    public String initiateOrderRentPayment(Long orderRentId) throws Exception {
        OrderRent orderRent = orderRentRepository.findById(orderRentId)
                .orElseThrow(() -> new Exception("OrderRent not found"));

        String returnUrl = "http://localhost:5173/success?orderID=" + orderRent.getId();
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
