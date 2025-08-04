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
    @NotNull(message = "ID cuôc hẹn không được trống")
    private Integer appointmentId;

    @NotNull(message = "Mạch áp hẹn không được trống")
    @Min(value = 30, message = "Mạch tối thiểu là 30")
    @Max(value = 200, message = "Mạch không quá 200")
    private Integer pulse;

    @Size(max = 20, message = "Huyết áp không được vượt quá 20 ký tự")
    private String bloodPressure;

    private String resultSummary;

    private Boolean isEligible;

    private String ineligibleReason;

    @NotNull(message = "Cân nặng không được để trống")
    private Double weight;

    private String suggestBloodVolume;

}
