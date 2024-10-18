package com.ToyRentalService.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "ToyName can not be blank!")
    @Column(nullable = false, unique = true)
    private String categoryName;

    @NotBlank(message = "description can not be blank!")
    private String description;

    private boolean isDelete = false;

    @ManyToMany(mappedBy = "categories",cascade = CascadeType.ALL)
    Set<Post> posts = new HashSet<>();
}
