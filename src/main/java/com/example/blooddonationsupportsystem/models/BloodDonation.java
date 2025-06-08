package com.example.blooddonationsupportsystem.models;


import com.example.blooddonationsupportsystem.utils.DonationStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "blood_donation")
@Data//toString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BloodDonation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer donationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDate donationDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bloodTypeId", nullable = false)
    private BloodType bloodType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bloodComponentId", nullable = false)
    private BloodComponent bloodComponent;

    @Min(1)
    @Column(nullable = false)
    private Integer volumeMl;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private DonationStatus status;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "healthCheckId")
    private HealthCheck healthCheck;
}
