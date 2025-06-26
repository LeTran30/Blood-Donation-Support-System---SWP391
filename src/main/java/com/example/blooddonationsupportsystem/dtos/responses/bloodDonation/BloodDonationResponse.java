package com.example.blooddonationsupportsystem.dtos.responses.bloodDonation;


import com.example.blooddonationsupportsystem.utils.DonationStatus;

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
