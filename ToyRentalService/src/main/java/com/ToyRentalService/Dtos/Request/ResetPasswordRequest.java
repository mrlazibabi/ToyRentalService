package com.ToyRentalService.Dtos.Request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ResetPasswordRequest {
    @Size(min = 6, message = "Password must be at least 6 characters!")
    private String password;
}
