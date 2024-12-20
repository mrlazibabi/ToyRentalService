package com.ToyRentalService.service;

import com.ToyRentalService.Dtos.Request.AccountRequest.*;
import com.ToyRentalService.entity.Account;
import com.ToyRentalService.enums.Role;
import com.ToyRentalService.exception.exceptions.NotFoundException;
import com.ToyRentalService.exception.exceptions.DuplicateEntity;
import com.ToyRentalService.Dtos.Response.AccountResponse;
import com.ToyRentalService.repository.AccountRepository;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    @Lazy
    PasswordEncoder passwordEncoder;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    TokenService tokenService;

    @Autowired
    EmailService emailService;

    public AccountResponse register(RegisterRequest registerRequest) {
        Account account = modelMapper.map(registerRequest, Account.class);
        try{
            String originPassword = registerRequest.getPassword();
            account.setPassword(passwordEncoder.encode(originPassword));
            account.setRole(Role.USER);
            Account newAccount = accountRepository.save(account);

            //sent mail
            EmailDetail emailDetail = new EmailDetail();
            emailDetail.setReceiver(newAccount);
            emailDetail.setSubject("Hello, We are happy to have you as our newest member!");
            emailDetail.setLink("https://www.google.com");
            emailService.sendMail(emailDetail);

            return modelMapper.map(newAccount, AccountResponse.class);
        }catch (Exception ex){
            System.out.println(ex.getMessage());
            if(ex.getMessage().contains(registerRequest.getEmail())){
                throw new DuplicateEntity("Duplicate Email!");
            }else if (ex.getMessage().contains(registerRequest.getPhone())){
                throw new DuplicateEntity("Duplicate Phone!");
            }else {
                throw new DuplicateEntity("Error!");
            }
        }
    }
    public AccountResponse createStaff(RegisterRequest registerRequest) {
        Account account = modelMapper.map(registerRequest, Account.class);
        try{
            String originPassword = registerRequest.getPassword();
            account.setPassword(passwordEncoder.encode(originPassword));
            account.setRole(Role.STAFF);
            Account newAccount = accountRepository.save(account);
            return modelMapper.map(newAccount, AccountResponse.class);
        }catch (Exception ex){
            System.out.println(ex.getMessage());
            if(ex.getMessage().contains(registerRequest.getEmail())){
                throw new DuplicateEntity("Duplicate Email!");
            }else if (ex.getMessage().contains(registerRequest.getPhone())){
                throw new DuplicateEntity("Duplicate Phone!");
            }else {
                throw new DuplicateEntity("Error!");
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
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
            ));

            Account account = (Account) authentication.getPrincipal();
            AccountResponse accountResponse = modelMapper.map(account, AccountResponse.class);
            accountResponse.setToken(tokenService.generateToken(account));
            return accountResponse;
        }catch (Exception ex){
            throw new EntityNotFoundException("Username or Password invalid!");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return accountRepository.findByUsername(username);
    }

//    public Account getCurrentAccount(){
//        Account account = (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        return accountRepository.findAccountById(account.getId());
//    }
public Account getCurrentAccount() {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    if (principal instanceof Account) {
        return (Account) principal;
    } else if (principal instanceof String && principal.equals("anonymousUser")) {
        throw new RuntimeException("User is not authenticated");
    } else {
        throw new RuntimeException("Invalid authentication principal");
    }
}


    public void forgotPassword(ForgotPasswordRequest forgotPasswordRequest){
        Account account = accountRepository.findByEmail(forgotPasswordRequest.getEmail());
        if(account == null){
            throw new NotFoundException("Account not found");
        }else {
            EmailDetail emailDetail = new EmailDetail();
            emailDetail.setReceiver(account);
            emailDetail.setSubject("Reset password");
            String baseUrl = System.getenv("RESET_PASSWORD_URL");
            emailDetail.setLink(baseUrl + "/resetpassword/?token=" + tokenService.generateToken(account));

            emailService.sendMail(emailDetail);
        }
    }

    public void resetPassword(ResetPasswordRequest resetPasswordRequest){
        Account account = getCurrentAccount();
        account.setPassword(passwordEncoder.encode(resetPasswordRequest.getPassword()));
        accountRepository.save(account);
    }

}
