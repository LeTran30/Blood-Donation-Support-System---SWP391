package com.example.blooddonationsupportsystem.dtos.request.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterRequest {

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    @Schema(description = "Email address", example = "user@example.com")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Schema(description = "Password, minimum 8 characters", example = "Password123!")
    private String password;

    @NotBlank(message = "Full name is required")
    @Size(min = 6, message = "Full name is required")
    @Schema(description = "Full name of the user", example = "Nguyen Van A")
    private String fullName;

    @Pattern(regexp = "^\\+?[0-9. ()-]{7,25}$", message = "Phone number is invalid")
    @Schema(description = "Phone number with country code", example = "+84 912 345 678")
    private String phoneNumber;
}
