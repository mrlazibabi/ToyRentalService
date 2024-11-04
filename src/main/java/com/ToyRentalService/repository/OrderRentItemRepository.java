package com.ToyRentalService.repository;

import com.ToyRentalService.entity.Account;
import com.ToyRentalService.entity.OrderItem;
import com.ToyRentalService.entity.OrderRentItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRentItemRepository extends JpaRepository<OrderRentItem,Long> {
    List<OrderRentItem> findByOrderRentCustomer(Account customer);
}
