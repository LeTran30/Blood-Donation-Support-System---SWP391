package com.example.blooddonationsupportsystem.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Entity
@Table(name = "distance_searches")
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference(value = "user-search")
    private User user;

    @ManyToOne
    @JoinColumn(name = "target_user_id", nullable = false)
    private User targetUser;

    @ManyToOne
    @JoinColumn(name = "blood_type_id", nullable = false)
    private BloodType bloodType;

    @Column(nullable = false)
    private Double distanceKM;

    @CreationTimestamp
    private Timestamp searchTime;
}
