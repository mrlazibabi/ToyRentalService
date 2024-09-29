package com.ToyRentalService.model;

import com.ToyRentalService.entity.Role;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "UserName can not be blank!")
    @Column(nullable = false, unique = true)
    private String username;

    @Email(message = "Invalid Email!")
    @Column(nullable = false, unique = true)
    private String email;

    @Pattern(regexp = "(84|0[3|5|7|8|9])+([0-9]{8})\\b", message = "Phone number invalid!")
    private String phone;

    @NotBlank(message = "UserName can not be blank!")
    @Size(min = 6, message = "Password must be at least 6 characters!")
    @Column(nullable = false)
    private String password;

    private Role role;
}
