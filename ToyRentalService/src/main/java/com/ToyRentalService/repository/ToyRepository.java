package com.ToyRentalService.repository;

import com.ToyRentalService.entity.Toy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.util.Optional;

@Repository
public interface ToyRepository extends JpaRepository<Toy, Long> {
    Optional<Toy> findByToyNameOrDescription(String toyName, String description, Pageable pageable);
    Toy findToyById(long id);
}
