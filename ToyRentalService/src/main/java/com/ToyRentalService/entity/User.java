package com.ToyRentalService.entity;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @NotBlank(message = "Name can not be blank!")
    String name;

    @NotBlank(message = "StudentCode can not be blank!")
    @Pattern(regexp = "SE\\d{6}")
    String studentCode;

    @Min(value =0, message = "Score must be higher than 0")
    @Max(value =0, message = "Score must be higher than 0")
    float score;
}
