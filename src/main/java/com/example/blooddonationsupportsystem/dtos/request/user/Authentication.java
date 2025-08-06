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
    @Email(message = "Email sai format")
    @NotBlank(message = "Cần cung cấp email")
    @Schema(description = "Email address", example = "user@example.com")
    private String email;

    @NotBlank(message = "Cần cung cấp mật khẩu")
    @Size(min = 8, message = "Mật khẩu tối thiểu 8 ký tự")
    @Schema(description = "Password, minimum 8 characters", example = "Password123!")
    private String password;
}
