package com.example.blooddonationsupportsystem.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Entity
@Table(name = "blood_requests")
@Data//toString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BloodRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer requestId;

    @ManyToOne
    @JoinColumn(name = "componentId", nullable = false)
    private BloodComponent component;

    @ManyToOne
    @JoinColumn(name = "bloodTypeId", nullable = false)
    private BloodType bloodType;

    private String urgencyLevel;
    private String status;

    @CreationTimestamp
    private Timestamp createdAt;
}
