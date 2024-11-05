package com.ToyRentalService.repository;

import com.ToyRentalService.entity.Toy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;


import java.util.Optional;

@Repository
public interface ToyRepository extends JpaRepository<Toy, Long>, JpaSpecificationExecutor<Toy> {
    Optional<Toy> findToyByToyNameOrDescription(String toyName, String description);
    Toy findToyById(long id);
    Page<Toy> findAll(Pageable pageable);
}