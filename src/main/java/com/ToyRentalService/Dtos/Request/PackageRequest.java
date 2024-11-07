package com.ToyRentalService.Dtos.Request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
@Data
@Getter
@Setter
public class PackageRequest {
    @NotBlank(message = "Package can not be blank!")
    @Column(nullable = false, unique = true)
    private String packageName;

    private double packagePrice;

    private int numberPost;

    private String description;
}
