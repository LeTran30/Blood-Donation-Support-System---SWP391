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
    @NotNull(message = "ID cuộc hẹn không được để trống")
    private Integer appointmentId;

    private Integer bloodTypeId;

    @NotNull(message = "Thể tích máu thực tế không được để trống")
    @Min(value = 250, message = "Thể tích máu thực tế không dưới 250ml")
    private Integer actualBloodVolume;
}
