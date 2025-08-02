package com.example.blooddonationsupportsystem.dtos.responses.healthCheck;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HealthCheckResponse {
    private Integer healthCheckId;
    private Integer pulse;
    private String bloodPressure;
    private String resultSummary;
    private LocalDateTime checkedAt;
    private Boolean isEligible;
    private String ineligibleReason;
    private Double weight;
    private String suggestBloodVolume;

    // User's blood type information
    private Integer bloodTypeId;
    private String bloodTypeName;
}
