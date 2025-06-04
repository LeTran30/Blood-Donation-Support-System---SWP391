package com.example.blooddonationsupportsystem.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.time.LocalDateTime;

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
    private Integer healthCheckId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @Min(30)
    @Max(200)
    @Column(nullable = false)
    private Integer pulse;

    @Column(length = 20)
    private String bloodPressure;

    @Column(columnDefinition = "TEXT")
    private String resultSummary;

    @Column(nullable = false)
    private LocalDateTime checkedAt = LocalDateTime.now();
}
