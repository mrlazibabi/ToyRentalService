package com.ToyRentalService.Dtos.Request.AccountRequest;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}
