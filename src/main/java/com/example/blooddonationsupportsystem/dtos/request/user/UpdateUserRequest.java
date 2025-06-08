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

    @NotBlank(message = "Full name is required")
    private String fullName;

    @Email(message = "Invalid email format")
    private String email;

    private String phoneNumber;

    private String address;

    private Gender gender; // optional: consider using Enum

    private Integer bloodTypeId;

    private LocalDate dateOfBirth;

    private Double latitude;  // for nearby search
    private Double longitude; // for nearby search
}