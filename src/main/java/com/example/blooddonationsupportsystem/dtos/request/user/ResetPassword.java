package com.example.blooddonationsupportsystem.dtos.request.user;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ResetPassword {
    private String email;
    private String password;
    private String otp;
}
