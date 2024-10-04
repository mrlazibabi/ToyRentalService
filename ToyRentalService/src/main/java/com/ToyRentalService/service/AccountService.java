package com.ToyRentalService.service;

import com.ToyRentalService.Dtos.Response.ResponseObject;
import com.ToyRentalService.entity.Account;
import com.ToyRentalService.entity.User;
import com.ToyRentalService.enums.Role;
import com.ToyRentalService.exception.exceptions.EntityNotFoundException;
import com.ToyRentalService.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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
}
