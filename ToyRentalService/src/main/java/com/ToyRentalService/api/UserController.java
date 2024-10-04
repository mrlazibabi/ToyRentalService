package com.ToyRentalService.api;

import com.ToyRentalService.entity.User;
import com.ToyRentalService.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/user")
@SecurityRequirement(name = "api")
public class UserController {
    @Autowired
    UserService userService;

    @PostMapping
    public ResponseEntity createUser(@Valid @RequestBody User user){
        User newUser = userService.createUser(user);
        return ResponseEntity.ok(newUser);
    }

    @GetMapping
    public ResponseEntity getAllUsers(){
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PutMapping("{UserId}")
    public ResponseEntity updateUser(@PathVariable long UserId,@Valid @RequestBody User user){
        User updateUser = userService.updateUser(UserId, user);
        return ResponseEntity.ok(updateUser);
    }

    @DeleteMapping("{UserId}")
    public ResponseEntity removeUser(@PathVariable long UserId){
        User removeUser = userService.removeUser(UserId);
        return ResponseEntity.ok(removeUser);
    }
}
