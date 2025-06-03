package com.example.blooddonationsupportsystem.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "health_check")
@Data//toString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HealthCheck {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long healthCheckId;

    @OneToOne
    private BloodDonation donation;

    private int pulse;
    private String bloodPressure;
    private float temperature;
    private float hemoglobinLevel;
    private String checkResult; // Normal, Warning, Disqualified
}
