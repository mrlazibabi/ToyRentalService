package com.ToyRentalService.service;

import com.ToyRentalService.entity.Account;
import com.ToyRentalService.enums.Role;
import com.ToyRentalService.exception.exceptions.DuplicateEntity;
import com.ToyRentalService.Dtos.Response.AccountResponse;
import com.ToyRentalService.Dtos.Request.LoginRequest;
import com.ToyRentalService.Dtos.Request.RegisterRequest;
import com.ToyRentalService.repository.AccountRepository;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class  AuthenticationService implements UserDetailsService {
    @Autowired
    AccountRepository accountRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    TokenService tokenService;

    public AccountResponse register(RegisterRequest registerRequest) {
        System.out.println(registerRequest.getPhone());
        Account account = modelMapper.map(registerRequest, Account.class);
        try{
            String originPassword = registerRequest.getPassword();
            account.setPassword(passwordEncoder.encode(originPassword));
            account.setRole(Role.USER);
            Account newAccount = accountRepository.save(account);
            return modelMapper.map(newAccount, AccountResponse.class);
        }catch (Exception ex){
            System.out.println(ex.getMessage());
            if(ex.getMessage().contains(registerRequest.getEmail())){
                throw new DuplicateEntity("Duplicate Email!");
            }else{
                throw new DuplicateEntity("Duplicate Phone!");
            }
        }
    }

    public List<Account> getAllAccounts() {
        List<Account> accounts = accountRepository.findAll();
        return accounts;
    }

    public AccountResponse login(LoginRequest loginRequest) {
        try{
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginRequest.getEmail(),
                    loginRequest.getPassword()
            ));

            Account account = (Account) authentication.getPrincipal();
            AccountResponse accountResponse = modelMapper.map(account, AccountResponse.class);
            accountResponse.setToken(tokenService.generateToken(account));
            return accountResponse;
        }catch (Exception ex){
            throw new EntityNotFoundException("Email or Password invalid!");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String Email) throws UsernameNotFoundException {
        return accountRepository.findByEmail(Email);
    }
}
