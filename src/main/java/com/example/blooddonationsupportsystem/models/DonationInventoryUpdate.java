package com.example.blooddonationsupportsystem.models;

import com.example.blooddonationsupportsystem.utils.DonationInventoryUpdateId;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.time.LocalDateTime;
@Entity
@Table(name = "donation_inventory_updates")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DonationInventoryUpdate {

    @EmbeddedId
    private DonationInventoryUpdateId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("bloodDonation")
    @JoinColumn(name = "donation_id", nullable = false)
    private BloodDonation bloodDonation;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("inventory")
    @JoinColumn(name = "inventory_id", nullable = false)
    private Inventory inventory;

    @Min(0)
    @Column(nullable = false)
    private Integer volumeAdded;

    @Column(nullable = false)
    private LocalDateTime addedAt = LocalDateTime.now();
}
