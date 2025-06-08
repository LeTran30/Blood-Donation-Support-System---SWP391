package com.example.blooddonationsupportsystem.dtos.responses.user;

import com.example.blooddonationsupportsystem.utils.Gender;
import com.example.blooddonationsupportsystem.utils.Role;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class UserDetailResponse {
    private String fullName;

    private String email;

    @Enumerated(EnumType.STRING)
    private Role role;

    private boolean status;

    private String phoneNumber;

    private String address;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private Double latitude;

    private Double longitude;

    private LocalDate dateOfBirth;

    private Integer bloodType;
}
