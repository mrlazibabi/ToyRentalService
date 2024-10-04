package com.ToyRentalService.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
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
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private long id;

    @NotBlank(message = "Name can not be blank!")
    private String name;

    private boolean isDelete = false;

//    @NotBlank(message = "StudentCode can not be blank!")
//    @Pattern(regexp = "SE\\d{6}")
//    private String studentCode;
//
//    @Min(value =0, message = "Score must be higher than 0")
//    @Max(value =0, message = "Score must be higher than 0")
//    private float score;
}
