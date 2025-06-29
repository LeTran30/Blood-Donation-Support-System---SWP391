package com.example.blooddonationsupportsystem.dtos.request.healthCheck;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class HealthCheckRequest {
    @NotNull(message = "Application id must not be null")
    private Integer appointmentId;

    @NotNull(message = "Pulse must not be null")
    @Min(value = 30, message = "Pulse must be at least 30")
    @Max(value = 200, message = "Pulse must be no more than 200")
    private Integer pulse;

    @Size(max = 20, message = "Blood pressure must not exceed 20 characters")
    private String bloodPressure;

    private String resultSummary;

    private Boolean isEligible;

    private String ineligibleReason;

    // Blood type ID to update user's blood type during health check
    private Integer bloodTypeId;
}
