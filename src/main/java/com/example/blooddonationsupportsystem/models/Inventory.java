package com.example.blooddonationsupportsystem.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventories",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"bloodTypeId", "componentId"})}
)
@Data//toString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer inventoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bloodTypeId", nullable = false)
    private BloodType bloodType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "componentId", nullable = false)
    private BloodComponent bloodComponent;

    @Min(0)
    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private LocalDateTime lastUpdated = LocalDateTime.now();
}
