package com.ToyRentalService.model;

import lombok.Data;

@Data
public class AccountResponse {
    long id;
    String username;
    String email;
    String phone;
    String token;
    String role;

}
