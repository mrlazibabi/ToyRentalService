package com.ToyRentalService.service;

import com.ToyRentalService.Dtos.Response.ResponseObject;
import com.ToyRentalService.entity.Account;
import com.ToyRentalService.enums.Role;
import com.ToyRentalService.exception.exceptions.EntityNotFoundException;
import com.ToyRentalService.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccountService {
    @Autowired
    AccountRepository accountRepository;
    public ResponseEntity<ResponseObject> getAllUser() {
        try {
            var list = accountRepository.findAllByStatusAndRole(true, Role.USER);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("Successful", "Found users", list));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject("failed", "Couldn't find users", null));
        }
    }
    //Update
    public Account updateAccount(long id, Account account){
        Account updateAccount = accountRepository.findAccountById(id);
        if(updateAccount == null){
            throw new EntityNotFoundException("Account not found!");
        }
        updateAccount.setEmail(account.getEmail());
        updateAccount.setPhone(account.getPhone());
        updateAccount.setPassword(account.getPassword());
        return accountRepository.save(updateAccount);
    }

    //Delete
    public Account removeAccount(long id){
        Account removeAccount = accountRepository.findAccountById(id);
        if(removeAccount == null){
            throw new EntityNotFoundException("Account not found!");
        }
        removeAccount.setActive(false);
        return accountRepository.save(removeAccount);
    }
    public List<Account> getAccountsByRole(Role role) {
        List<Account> accounts = accountRepository.findAllByStatusAndRole(true, role);
        if (accounts.isEmpty()) {
            throw new EntityNotFoundException("No accounts found for role: " + role);
        }
        return accounts;
    }
    // Tạo tài khoản mới
    public Account createAccount(Account account) {
        return accountRepository.save(account);
    }

    // Lấy tất cả tài khoản
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    // Lấy tài khoản theo ID
    public Optional<Account> getAccountById(Long id) {
        return accountRepository.findById(id);
    }

    // Cập nhật tài khoản
    public Account updateAccount(Long id, Account accountDetails) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Account not found"));
        account.setEmail(accountDetails.getEmail());
        account.setPhone(accountDetails.getPhone());
        account.setPassword(accountDetails.getPassword());
        return accountRepository.save(account);
    }

    // Xóa tài khoản
    public void deleteAccount(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Account not found"));
        accountRepository.delete(account);
    }

    // Khôi phục tài khoản (giả định tài khoản chỉ bị vô hiệu hóa)
    public Account restoreAccount(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Account not found"));
        account.setActive(true);
        return accountRepository.save(account);
    }
}
