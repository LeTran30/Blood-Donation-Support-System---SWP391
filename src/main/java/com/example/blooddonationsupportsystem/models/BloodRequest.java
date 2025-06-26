package com.example.blooddonationsupportsystem.models;

import com.example.blooddonationsupportsystem.utils.RequestStatus;
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
    @JoinColumn(name = "component_id", nullable = false)
    private BloodComponent component;

    @ManyToOne
    @JoinColumn(name = "blood_type_id", nullable = false)
    private BloodType bloodType;

    private String urgencyLevel;

    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    @CreationTimestamp
    private Timestamp createdAt;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;
}
