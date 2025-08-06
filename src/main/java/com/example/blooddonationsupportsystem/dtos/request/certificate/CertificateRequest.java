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

    @NotNull(message = "ID người dùng không được để trống")
    private Integer userId;

    @NotNull(message = "Loại chứng chỉ không được để trống")
    private CertificateType certificateType;

    @NotBlank(message = "Mô tả không được để trống")
    private String description;
}