package com.ToyRentalService.Dtos.Response;

import lombok.Data;

import java.util.Date;

@Data
public class AccountResponse {
    long id;
    String username;
    String phone;
    String email;
    String address;
    Date dob;
    String image;
    String token;
    String role;
}
