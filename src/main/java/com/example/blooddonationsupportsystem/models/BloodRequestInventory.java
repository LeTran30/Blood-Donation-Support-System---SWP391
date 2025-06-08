package com.example.blooddonationsupportsystem.models;

import com.example.blooddonationsupportsystem.utils.BloodRequestInventoryKey;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;

@Entity
@Table(name = "blood_request_inventories")
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BloodRequestInventory {
    @EmbeddedId
    private BloodRequestInventoryKey id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("requestId")
    @JoinColumn(name = "request_id")
    private BloodRequest bloodRequest;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("inventoryId")
    @JoinColumn(name = "inventory_id")
    private Inventory inventory;

    @Column(nullable = false)
    @Min(0)
    private Integer allocatedQuantity;
}
