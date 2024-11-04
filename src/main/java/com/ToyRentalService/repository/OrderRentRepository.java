package com.ToyRentalService.repository;

import com.ToyRentalService.entity.OrderRent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRentRepository extends JpaRepository<OrderRent, Long> {
}
