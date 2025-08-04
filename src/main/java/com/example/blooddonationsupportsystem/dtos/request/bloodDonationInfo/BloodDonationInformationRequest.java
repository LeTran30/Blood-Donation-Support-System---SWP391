package com.example.blooddonationsupportsystem.dtos.request.bloodDonationInfo;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BloodDonationInformationRequest {
    @NotNull(message = "Appointment ID is required")
    private Integer appointmentId;

    private Integer bloodTypeId;

    @NotNull(message = "Actual blood volume is required")
    @Min(value = 1, message = "Actual blood volume must be at least 1")
    private Integer actualBloodVolume;
}
