package com.example.blooddonationsupportsystem.models;

import com.example.blooddonationsupportsystem.utils.DonationInventoryUpdateId;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.time.LocalDateTime;
@Entity
@Table(name = "donation_inventory_update")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DonationInventoryUpdate {

    @EmbeddedId
    private DonationInventoryUpdateId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("bloodDonation")
    @JoinColumn(name = "donationId", nullable = false)
    private BloodDonation bloodDonation;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("inventory")
    @JoinColumn(name = "inventoryId", nullable = false)
    private Inventory inventory;

    @Min(0)
    @Column(nullable = false)
    private Integer volumeAdded;

    @Column(nullable = false)
    private LocalDateTime addedAt = LocalDateTime.now();
}
