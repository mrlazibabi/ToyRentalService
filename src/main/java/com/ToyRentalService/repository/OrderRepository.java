package com.ToyRentalService.repository;

import com.ToyRentalService.entity.Account;
import com.ToyRentalService.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Orders, Long> {
    List<Orders> findOrderByCustomer(Account account);
}
