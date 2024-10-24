package com.ToyRentalService.Dtos.Request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoryRequest {
    @NotBlank(message = "ToyName can not be blank!")
    @Column(nullable = false, unique = true)
    private String categoryName;

    @NotBlank(message = "description can not be blank!")
    private String description;

    //private boolean isDelete = false;
}
