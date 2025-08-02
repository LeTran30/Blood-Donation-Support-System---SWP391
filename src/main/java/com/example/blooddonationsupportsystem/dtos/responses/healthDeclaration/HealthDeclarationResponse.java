package com.example.blooddonationsupportsystem.dtos.responses.healthDeclaration;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HealthDeclarationResponse {
    private Integer healthDeclarationId;
    private Integer appointmentId;
    private Boolean hasBloodTransmittedDisease;
    private Boolean hasChronicDisease;
    private String currentMedications;
    private Boolean hasTattooAcupuncture;
    private Boolean hasRecentVaccine;
    private Boolean hasTravelAbroad;
    private Boolean hasUnsafeSex;
    private Boolean isFirstBlood;
    private Boolean isPregnantOrBreastfeeding;
    private Boolean isMenstruating;
    private Integer userId;
    private String userName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
