package com.ToyRentalService.Dtos.Request.AccountRequest;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "UserName can not be blank!")
    @Column(nullable = false, unique = true)
    private String username;

    @Pattern(regexp = "(84|0[3|5|7|8|9])+([0-9]{8})\\b", message = "Phone number invalid!")
    private String phone;

    @Email(message = "Invalid Email!")
    @Column(nullable = false, unique = true)
    private String email;

    @JsonIgnore
    private String image;

    @Size(min = 6, message = "Password must be at least 6 characters!")
    @Column(nullable = false)
    private String password;
}
