package com.example.blooddonationsupportsystem.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "donation_inventory_update")
@Data//toString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DonationInventoryUpdate {
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "donationId", nullable = false)
    private BloodDonation bloodDonation;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventoryId", nullable = false)
    private Inventory inventory;

    @Min(0)
    @Column(nullable = false)
    private Integer volumeAdded;

    @Column(nullable = false)
    private LocalDateTime addedAt = LocalDateTime.now();

}
