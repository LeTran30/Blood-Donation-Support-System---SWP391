package com.example.blooddonationsupportsystem.dtos.responses.inventoryAllocation;

import com.example.blooddonationsupportsystem.utils.BloodRequestInventoryKey;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryAllocationResponse {
    private Integer requestId;
    private Integer inventoryId;
    private Integer bloodTypeId;
    private Integer allocatedQuantity;
    private LocalDate addedDate;
    private LocalDate expiryDate;
}
