package com.ToyRentalService.service;

import com.ToyRentalService.entity.Account;
import com.ToyRentalService.exception.DuplicateEntity;
import com.ToyRentalService.model.LoginRequestDto;
import com.ToyRentalService.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthenticationService implements UserDetailsService {
    @Autowired
    AccountRepository accountRepository;

    public Account register(Account account) {
        try{
            Account newAccount = accountRepository.save(account);
            return newAccount;
        }catch (Exception ex){
            if(ex.getMessage().contains(account.getEmail())){
                throw new DuplicateEntity("Duplicate Email!");
            }else{
                throw new DuplicateEntity("Duplicate Phone!");
            }
        }
    }

    public List<Account> getAllUsers() {
        List<Account> accounts = accountRepository.findAll();
        return accounts;
    }

    public Account loginUser(LoginRequestDto loginRequestDto) {
        try{
            Account account = accountRepository.findByEmail(loginRequestDto.getEmail());

            if (account == null ) {
                return null;
            }
            return account;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return null;
    }
}
