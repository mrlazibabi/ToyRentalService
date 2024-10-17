package com.ToyRentalService.api;

import com.ToyRentalService.Dtos.Request.AccountRequest.AccountUpdateRequest;
import com.ToyRentalService.entity.Account;
import com.ToyRentalService.enums.Role;
import com.ToyRentalService.service.AccountService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@CrossOrigin("*")
@SecurityRequirement(name = "api")
@RequestMapping("/api/v1/account")
public class AccountController {

    @Autowired
    AccountService accountService;

    @GetMapping
    public Page<Account> getAllAccounts(
            @RequestParam(required = false) Role role, // Đổi từ String sang Role
            @RequestParam(defaultValue = "false") boolean isActive,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return accountService.getAllAccounts(role, isActive, page, size);
    }

     //Lấy tài khoản theo ID
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
    @DeleteMapping("/{id}/delete")
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

