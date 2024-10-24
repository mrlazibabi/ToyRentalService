package com.ToyRentalService.repository;

import com.ToyRentalService.entity.OrderHistory;
import com.ToyRentalService.enums.OrderType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderHistoryRepository extends JpaRepository<OrderHistory, Long> {
    List<OrderHistory> findByOrderType(OrderType type);
}
