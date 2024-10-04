package com.ToyRentalService.entity;

import com.ToyRentalService.enums.Role;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "user")
public class Account implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private long id;

    @NotBlank(message = "UserName can not be blank!")
    @Column(nullable = false, unique = true)
    private String username;

    @Pattern(regexp = "(84|0[3|5|7|8|9])+([0-9]{8})\\b", message = "Phone number invalid!")
    private String phone;
    @Column(name = "status")
    private Boolean status;
    @Email(message = "Invalid Email!")
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "Address can not be blank!")
    private String address;

    @NotBlank(message = "Date of Birth can not be blank!")
    private Date dob;

    @Pattern(regexp = "(.*/)*.+\\.(png|jpg|gif|bmp|jpeg|PNG|JPG|GIF|BMP|JPEG)$", message = "File invalid!")
    private String image;

    private int postCount;

    @Min(value =0, message = "Score must be higher than 0")
    @Max(value =0, message = "Score must be higher than 0")
    private float point;

    @NotBlank(message = "UserName can not be blank!")
    @Size(min = 6, message = "Password must be at least 6 characters!")
    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    Role role;

    private boolean isActive = true;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        if (this.role != null) authorities.add(new SimpleGrantedAuthority(this.role.toString()));
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
