package com.ToyRentalService.repository;

import com.ToyRentalService.entity.Account;
import com.ToyRentalService.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByCustomer(Account customer);
}
