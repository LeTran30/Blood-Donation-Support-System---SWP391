package com.example.blooddonationsupportsystem.dtos.request.bloodDonation;

import com.example.blooddonationsupportsystem.models.BloodType;
import com.example.blooddonationsupportsystem.models.HealthCheck;
import com.example.blooddonationsupportsystem.models.User;
import com.example.blooddonationsupportsystem.utils.DonationStatus;
import jakarta.persistence.*;
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
public class BloodDonationRequest {

    @NotNull(message = "User is required.")
    private Integer user;

    @NotNull(message = "Donation date is required.")
    private LocalDate donationDate;

    @NotNull(message = "Blood type is required.")
    private Integer bloodType;

    @NotNull(message = "Volume in ml is required.")
    @Min(value = 50, message = "Minimum donation volume is 50ml.")
    private Integer volumeMl;

    @NotNull(message = "Donation status is required.")
    private DonationStatus status;

    @NotNull(message = "Health check information is required.")
    private Integer healthCheck;

}
