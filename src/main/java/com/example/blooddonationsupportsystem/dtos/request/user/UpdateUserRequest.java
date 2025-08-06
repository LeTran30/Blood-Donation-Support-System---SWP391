package com.example.blooddonationsupportsystem.dtos.request.user;

import com.example.blooddonationsupportsystem.utils.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateUserRequest {

    @NotBlank(message = "Cần cung cấp họ và tên")
    private String fullName;

    @Email(message = "Email không hợp lệ")
    private String email;

    private String phoneNumber;

    private String address;

    private Gender gender; // optional: consider using Enum

    private Integer bloodTypeId;

    private LocalDate dateOfBirth;

    private Double latitude;  // for nearby search
    private Double longitude; // for nearby search

    private String citizenId;

    private String job;
}