package com.example.blooddonationsupportsystem.models;

import com.example.blooddonationsupportsystem.utils.InventoryStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventories", uniqueConstraints = @UniqueConstraint(columnNames = "batchNumber"))
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "component_id", nullable = false)
    private BloodComponent bloodComponent;

    @Min(0)
    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private LocalDateTime lastUpdated = LocalDateTime.now();

    @Column(name = "added_date", nullable = false)
    private LocalDate addedDate;

    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;

    @Column(name = "batch_number", unique = true, nullable = false)
    private String batchNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InventoryStatus status;

    @PrePersist
    public void prePersist() {
        lastUpdated = LocalDateTime.now();
        if (addedDate == null) addedDate = LocalDate.now();
        if (status == null) status = InventoryStatus.AVAILABLE;
    }


    @PreUpdate
    public void preUpdate() {
        lastUpdated = LocalDateTime.now();
    }

}
