package com.ToyRentalService.repository;

import com.ToyRentalService.entity.Account;
import com.ToyRentalService.entity.OrderItem;
import com.ToyRentalService.enums.OrderType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem,Long> {
    List<OrderItem> findByOrdersCustomer(Account customer);
}
