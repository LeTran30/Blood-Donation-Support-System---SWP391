package com.example.blooddonationsupportsystem.models;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "donation_histories")
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference(value = "user-history")
    private User user;

    @Min(0)
    @Column(nullable = false)
    private Integer totalVolume = 0;

    @Min(0)
    @Column(nullable = false)
    private Integer totalDonations = 0;

    private LocalDate lastDonationDate;
}
