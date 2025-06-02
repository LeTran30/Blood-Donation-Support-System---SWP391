package com.example.blooddonationsupportsystem.dtos.responses.user;


import com.example.blooddonationsupportsystem.utils.Role;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class UserResponse {
    private String fullName;
    private String email;
    @Enumerated(EnumType.STRING)
    private Role role;
    private boolean status;
}
