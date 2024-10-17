package com.ToyRentalService.Dtos.Request.AccountRequest;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class AccountUpdateRequest {
    private String username;

    @Pattern(regexp = "(84|0[3|5|7|8|9])+([0-9]{8})\\b", message = "Phone number invalid!")
    private String phone;
    private String email;

    private String image;

    private String address;
    private String password;
}
