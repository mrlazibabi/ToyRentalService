package com.ToyRentalService.repository;
import com.ToyRentalService.entity.Account;
import com.ToyRentalService.entity.RentalPackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RentalPackageRepository extends JpaRepository<RentalPackage, Long> {
    Optional<RentalPackage> findByPackageNameOrDescription(String packageName, String description);
    RentalPackage findPackageById(long id);
    List<RentalPackage> findByAccount(Account account);
}
