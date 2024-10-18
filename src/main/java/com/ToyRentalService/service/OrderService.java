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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
        orders.setType(OrderType.BUYPOST);
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
        return orderRepository.save(orders);
    }
}
