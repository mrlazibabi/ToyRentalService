package com.ToyRentalService.api;

import com.ToyRentalService.entity.Account;
import com.ToyRentalService.service.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/api")
public class AuthController {
    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity register(@Valid @RequestBody Account account) {

        authenticationService.register(account);
        return ResponseEntity.ok("User registered successfully");
    }

    @GetMapping("/user")
    public ResponseEntity getAllUsers(){
        List<Account> accounts = authenticationService.getAllUsers();
        return ResponseEntity.ok(accounts);
    }

//    @PostMapping("/login")
//    public ResponseEntity<String> loginUser(@RequestBody LoginRequestDto loginRequestDto) {
//        User authenticatedUser = authenticationService.loginUser(loginRequestDto);
//
//        if (authenticatedUser == null) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
//        }
//
//        return ResponseEntity
//    }

}