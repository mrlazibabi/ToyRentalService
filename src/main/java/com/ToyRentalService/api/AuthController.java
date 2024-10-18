package com.ToyRentalService.api;


import com.ToyRentalService.Dtos.Request.*;
import com.ToyRentalService.Dtos.Request.AccountRequest.ForgotPasswordRequest;
import com.ToyRentalService.Dtos.Request.AccountRequest.LoginRequest;
import com.ToyRentalService.Dtos.Request.AccountRequest.RegisterRequest;
import com.ToyRentalService.Dtos.Request.AccountRequest.ResetPasswordRequest;
import com.ToyRentalService.entity.Account;
import com.ToyRentalService.Dtos.Response.AccountResponse;
import com.ToyRentalService.service.AuthenticationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/api")
@SecurityRequirement(name = "api")
public class AuthController {
    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity register(@Valid @RequestBody RegisterRequest registerRequest) {

        AccountResponse newAcccount = authenticationService.register(registerRequest);
        return ResponseEntity.ok(newAcccount);
    }

    @GetMapping("/account")
    public ResponseEntity getAllAccounts(){
        List<Account> accounts = authenticationService.getAllAccounts();
        return ResponseEntity.ok(accounts);
    }

    @PostMapping("/login")
    public ResponseEntity login(@Valid @RequestBody LoginRequest loginRequest) {

        AccountResponse newAcccount = authenticationService.login(loginRequest);
        return ResponseEntity.ok(newAcccount);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity forgotPassword(@Valid @RequestBody ForgotPasswordRequest forgotPasswordRequest){
        authenticationService.forgotPassword(forgotPasswordRequest);
        return ResponseEntity.ok("Success");
    }

    @PostMapping("/reset-password")
    public ResponseEntity resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest){
        authenticationService.resetPassword(resetPasswordRequest);
        return ResponseEntity.ok("Reset Password Successfully");
    }
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/staff")
    public ResponseEntity createStaff(@Valid @RequestBody RegisterRequest registerRequest) {

        AccountResponse newAcccount = authenticationService.createStaff(registerRequest);
        return ResponseEntity.ok(newAcccount);
    }
}