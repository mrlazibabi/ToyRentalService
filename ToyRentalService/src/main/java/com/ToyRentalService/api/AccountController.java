package com.ToyRentalService.api;

import com.ToyRentalService.Dtos.Request.AccountUpdateRequest;
import com.ToyRentalService.entity.Account;
import com.ToyRentalService.enums.Role;
import com.ToyRentalService.service.AccountService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin("*")
@SecurityRequirement(name = "api")
@RequestMapping("/api/v1/account")
public class AccountController {

    @Autowired
    AccountService accountService;

    // API lấy danh sách User
//    @GetMapping("/users")
//    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
//    public ResponseEntity<List<Account>> getAllUsers() {
//        List<Account> users = accountService.getAccountsByRole(Role.USER);
//        return ResponseEntity.ok(users);
//    }
//    // API lấy danh sách Staff
//    @GetMapping("/staffs")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<List<Account>> getAllStaffs() {
//        List<Account> staffs = accountService.getAccountsByRole(Role.STAFF);
//        return ResponseEntity.ok(staffs);
//    }

    // Lấy tất cả tài khoản
    @GetMapping
    public ResponseEntity<List<Account>> getAllAccounts() {
        List<Account> accounts = accountService.getAllAccounts();
        return ResponseEntity.ok(accounts);
    }

    // Lấy tài khoản theo ID
    @GetMapping("/{id}")
    public ResponseEntity<Account> getAccountById(@PathVariable Long id) {
        Optional<Account> account = accountService.getAccountById(id);
        return account.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Cập nhật tài khoản
    @PutMapping("/{id}")
    public ResponseEntity<?> updateAccount(@PathVariable Long id, @RequestBody AccountUpdateRequest accountUpdateRequest) {
        Account updatedAccount = accountService.updateAccount(id, accountUpdateRequest);
        return ResponseEntity.ok("Account updated successfully");
    }


    // Xóa tài khoản
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long id) {
        accountService.removeAccount(id);
        return ResponseEntity.noContent().build();
    }

    // Khôi phục tài khoản
    @PutMapping("/{id}/restore")
    public ResponseEntity<Account> restoreAccount(@PathVariable Long id) {
        Account restoredAccount = accountService.restoreAccount(id);
        return ResponseEntity.ok(restoredAccount);
    }
}

