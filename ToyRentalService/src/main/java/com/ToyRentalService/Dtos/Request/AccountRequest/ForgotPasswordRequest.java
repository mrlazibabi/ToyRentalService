package com.ToyRentalService.Dtos.Request.AccountRequest;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class ForgotPasswordRequest {
    @Email(message = "Invalid Email!")
    private String email;
}
