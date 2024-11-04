package com.ToyRentalService.repository;

import com.ToyRentalService.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
}
