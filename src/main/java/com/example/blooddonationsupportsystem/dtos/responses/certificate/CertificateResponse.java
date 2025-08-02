package com.example.blooddonationsupportsystem.dtos.responses.certificate;
import com.example.blooddonationsupportsystem.utils.CertificateType;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CertificateResponse {
    private Integer certificateId;
    private Integer userId;
    private String userName;
    private CertificateType certificateType;
    private String description;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
}
