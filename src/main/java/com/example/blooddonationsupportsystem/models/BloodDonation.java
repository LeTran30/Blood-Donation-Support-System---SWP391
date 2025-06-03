package com.example.blooddonationsupportsystem.models;


import jakarta.persistence.*;
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
    private Long donationId;

    @ManyToOne
    private User user;

    private LocalDate donationDate;

    @ManyToOne
    private BloodType bloodType;

    private int volumeML;
    private String status; // Completed, Rejected
}
