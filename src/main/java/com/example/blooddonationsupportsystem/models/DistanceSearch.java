package com.example.blooddonationsupportsystem.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Entity
@Table(name = "distance_search")
@Data//toString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DistanceSearch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer searchId;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "targetUserId", nullable = false)
    private User targetUser;

    @ManyToOne
    @JoinColumn(name = "bloodTypeId", nullable = false)
    private BloodType bloodType;

    @Column(nullable = false)
    private Double distanceKM;

    @CreationTimestamp
    private Timestamp searchTime;
}
