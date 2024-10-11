package com.ToyRentalService.repository;

import com.ToyRentalService.entity.Toy;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

@Repository
public interface ToyRepository extends JpaRepository<Toy, Long> {
//    Optional<Toy> findByToyNameOrDescription(String toyName, String description, Pageable pageable);
    Optional<Toy> findByToyNameOrDescription(String toyName, String description);
    Toy findToyById(long id);
}
