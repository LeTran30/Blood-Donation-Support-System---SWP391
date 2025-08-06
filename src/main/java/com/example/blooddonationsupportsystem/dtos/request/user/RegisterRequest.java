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

    @Email(message = "Email sai format")
    @NotBlank(message = "Cần cung cấp email")
    @Schema(description = "Email address", example = "user@example.com")
    private String email;

    @NotBlank(message = "Cần cung cấp mật khẩu")
    @Size(min = 8, message = "Mật khẩu tối thiểu 8 ký tự")
    @Schema(description = "Password, minimum 8 characters", example = "Password123!")
    private String password;

    @NotBlank(message = "Cần cung cấp họ và tên")
    @Size(min = 8, message = "Họ và tên tối thiểu 8 ký tự")
    @Schema(description = "Full name of the user", example = "Nguyen Van A")
    private String fullName;

    @Pattern(regexp = "^\\+?[0-9. ()-]{7,25}$", message = "Số điện thoại không hợp lệ")
    @Schema(description = "Phone number with country code", example = "+84 912 345 678")
    private String phoneNumber;
}
