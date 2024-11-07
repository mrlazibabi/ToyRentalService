package com.ToyRentalService.api;

import com.ToyRentalService.Dtos.Request.AccountRequest.AccountUpdateRequest;
import com.ToyRentalService.Dtos.Request.AccountRequest.UpdateFCMRequest;
import com.ToyRentalService.entity.Account;
import com.ToyRentalService.enums.Role;
import com.ToyRentalService.service.AccountService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
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

    @GetMapping
    public Page<Account> getAllAccounts(
            @RequestParam(required = false) Role role, // Đổi từ String sang Role
            @RequestParam(defaultValue = "false") boolean isActive,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return accountService.getAllAccounts(role, isActive, page, size);
    }

     //Lấy tài khoản theo ID
    @GetMapping("/user/{id}")
    public ResponseEntity<Account> getAccountById(@PathVariable Long id) {
        Optional<Account> account = accountService.getAccountById(id);
        return account.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/name")
    public ResponseEntity<Account> getAccountByUsername(@PathVariable String username){
        Optional<Account> account = accountService.getAccountByUsername(username);
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
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/staff")
    public ResponseEntity<List<Account>> getAllStaff() {
        List<Account> staffAccounts = accountService.getAccountsByRoles(Role.STAFF);
        return ResponseEntity.ok(staffAccounts);
    }

    @GetMapping("/user")
    public ResponseEntity<List<Account>> getAllUsers() {
        List<Account> userAccounts = accountService.getAccountsByRoles(Role.USER);
        System.out.println("User Accounts: " + userAccounts); // Ghi log
        return ResponseEntity.ok(userAccounts);
    }

    @PatchMapping("fcm")
    public ResponseEntity updateFCM(@RequestBody UpdateFCMRequest updateFCMRequest){
        Account  account = accountService.updateFCM(updateFCMRequest);
        return ResponseEntity.ok(account);
    }
}

