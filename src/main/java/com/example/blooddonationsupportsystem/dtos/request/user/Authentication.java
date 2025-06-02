package com.example.blooddonationsupportsystem.dtos.request.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Authentication {
    @Email(message = "email invalid format")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "password must have 8 character")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;
}
