package com.example.blooddonationsupportsystem.dtos.responses.eligibilityCheck;

import lombok.*;

import java.time.LocalDate;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EligibilityCheckResponse {

    private Integer checkId;

    private Integer user;

    private LocalDate checkDate;

    private Boolean isEligible;

    private String reason;
}
