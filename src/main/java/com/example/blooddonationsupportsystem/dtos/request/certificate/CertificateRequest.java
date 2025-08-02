package com.example.blooddonationsupportsystem.dtos.request.certificate;

import com.example.blooddonationsupportsystem.utils.CertificateType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CertificateRequest {

    @NotNull(message = "User ID must not be null")
    private Integer userId;

    @NotNull(message = "Certificate type must not be null")
    private CertificateType certificateType;

    @NotBlank(message = "Description must not be blank")
    private String description;
}