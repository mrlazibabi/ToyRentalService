package com.ToyRentalService.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
public class RentalPackage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "Package can not be blank!")
    @Column(nullable = false, unique = true)
    private String packageName;

    private double packagePrice;

    private int numberPost;

    private String description;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;


}
