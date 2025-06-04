package com.example.blooddonationsupportsystem.models;


import jakarta.persistence.*;
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
    private Long historyId;

    @OneToOne
    private User user;

    private int totalVolume;
    private int totalDonations;
    private LocalDate lastDonationDate;
}
