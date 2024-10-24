package com.ToyRentalService.Dtos.Request.AccountRequest;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}
