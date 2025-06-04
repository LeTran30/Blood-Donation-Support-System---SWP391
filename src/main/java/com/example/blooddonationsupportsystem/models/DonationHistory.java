package com.example.blooddonationsupportsystem.models;


import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "donation_history")
@Data//toString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DonationHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer historyId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false, unique = true)
    private User user;

    @Min(0)
    @Column(nullable = false)
    private Integer totalVolume = 0;

    @Min(0)
    @Column(nullable = false)
    private Integer totalDonations = 0;

    private LocalDate lastDonationDate;
}
