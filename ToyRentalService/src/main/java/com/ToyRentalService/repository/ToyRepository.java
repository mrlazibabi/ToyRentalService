package com.ToyRentalService.repository;

import com.ToyRentalService.entity.Toy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ToyRepository extends JpaRepository<Toy, Long> {
    Optional<Toy> findByToyname(String toyName);
    Toy findToyById(long toyId);
}
