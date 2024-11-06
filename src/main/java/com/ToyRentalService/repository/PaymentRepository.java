package com.ToyRentalService.repository;

import com.ToyRentalService.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByOrder_Customer_IdOrOrderRent_Customer_Id(Long orderCustomerId, Long orderRentCustomerId);

}
