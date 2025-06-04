package com.example.blooddonationsupportsystem.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import lombok.*;

@Entity
@Table(name = "blood_request_inventory")
@Data//toString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BloodRequestInventory {
    @Id
    private Integer requestId;

    @Id
    private Integer inventoryId;

    @Min(0)
    private Integer allocatedQuantity;
}
