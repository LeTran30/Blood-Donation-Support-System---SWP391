package com.example.blooddonationsupportsystem.dtos.request.eligibilityCheck;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EligibilityCheckRequest {

//    private Integer checkId;

    @NotNull(message = "User ID cannot be null")
    private Integer user;

    @NotNull(message = "Check date cannot be null")
    private LocalDate checkDate;

    @NotNull(message = "Eligibility status cannot be null")
    private Boolean isEligible;

    @Size(max = 500, message = "Reason must not exceed 500 characters")
    private String reason;
}
