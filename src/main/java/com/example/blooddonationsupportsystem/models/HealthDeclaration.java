package com.example.blooddonationsupportsystem.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "health_declarations")
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HealthDeclaration extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer healthDeclarationId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id", nullable = false)
    private Appointment appointment;

    @Column(nullable = false)
    private Boolean hasBloodTransmittedDisease;

    @Column(nullable = false)
    private Boolean hasChronicDisease;

    @Column(columnDefinition = "TEXT")
    private String currentMedications;

    @Column(nullable = false)
    private Boolean hasTattooAcupuncture;

    @Column(nullable = false)
    private Boolean hasRecentVaccine;

    @Column(nullable = false)
    private Boolean hasTravelAbroad;

    @Column(nullable = false)
    private Boolean hasUnsafeSex;

    @Column(nullable = false)
    private Boolean isFirstBlood;

    @Column(nullable = true)
    private Boolean isPregnantOrBreastfeeding;

    @Column(nullable = true)
    private Boolean isMenstruating;
}