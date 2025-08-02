package com.example.blooddonationsupportsystem.dtos.responses.bloodDonationInfo;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BloodDonationInformationResponse {
    private Integer bloodDonationInformationId;
    private Integer appointmentId;
    private Integer bloodTypeId;
    private String bloodTypeName;
    private Integer actualBloodVolume;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer userId;
    private String userName;
}