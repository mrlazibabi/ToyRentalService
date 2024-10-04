package com.ToyRentalService.service;

import com.ToyRentalService.entity.Account;
import com.ToyRentalService.entity.User;
import com.ToyRentalService.exception.exceptions.EntityNotFoundException;
import com.ToyRentalService.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountService {
    @Autowired
    AccountRepository accountRepository;

    //Update
    public Account updateAccount(long id, Account account){
        Account updateAccount = accountRepository.findAccounById(id);
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
        Account removeAccount = accountRepository.findAccounById(id);
        if(removeAccount == null){
            throw new EntityNotFoundException("Account not found!");
        }
        removeAccount.setActive(false);
        return accountRepository.save(removeAccount);
    }
}
