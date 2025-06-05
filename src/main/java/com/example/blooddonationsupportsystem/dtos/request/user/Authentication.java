package com.example.blooddonationsupportsystem.dtos.request.user;

import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "Email address", example = "user@example.com")
    private String email;

    @NotBlank(message = "password must have 8 character")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Schema(description = "Password, minimum 8 characters", example = "Password123!")
    private String password;
}
