package com.ToyRentalService.repository;

import com.ToyRentalService.entity.Account;
import com.ToyRentalService.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByUsername(String username);

    Account findByEmail(String email);
    List<Account> findAllByStatusAndRole(Boolean status, Role role);
    List<Account> findByRole(Role role);
    Account findAccountById(long id);
    Page<Account> findByRoleAndIsActive(Role role, boolean isActive, Pageable pageable);
}
