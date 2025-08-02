package com.example.blooddonationsupportsystem.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventories",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"bloodTypeId"})}
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
    @JoinColumn(name = "blood_type_id", nullable = false)
    private BloodType bloodType;

    @Min(0)
    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private LocalDateTime lastUpdated = LocalDateTime.now();

    @Column(name = "added_date", nullable = true)
    private LocalDate addedDate;

    @Column(name = "expiry_date", nullable = true)
    private LocalDate expiryDate;

    @PrePersist
    public void prePersist() {
        lastUpdated = LocalDateTime.now();
        if (addedDate == null) {
            addedDate = LocalDate.now();
        }
    }

    @PreUpdate
    public void preUpdate() {
        lastUpdated = LocalDateTime.now();
    }

}
