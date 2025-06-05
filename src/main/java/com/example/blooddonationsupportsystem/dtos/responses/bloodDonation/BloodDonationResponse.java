package com.example.blooddonationsupportsystem.dtos.responses.bloodDonation;

import com.example.blooddonationsupportsystem.dtos.responses.user.UserResponse;
import com.example.blooddonationsupportsystem.models.BloodType;
import com.example.blooddonationsupportsystem.models.HealthCheck;
import com.example.blooddonationsupportsystem.models.User;
import com.example.blooddonationsupportsystem.utils.DonationStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BloodDonationResponse {

    private Integer donationId;

    private Integer user;

    private LocalDate donationDate;

    private Integer bloodType;

    private Integer volumeMl;

    private DonationStatus status;

    private Integer healthCheck;
}
