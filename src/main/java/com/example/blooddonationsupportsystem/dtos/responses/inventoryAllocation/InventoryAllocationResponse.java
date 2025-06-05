package com.example.blooddonationsupportsystem.dtos.responses.inventoryAllocation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryAllocationResponse {
    private Integer allocationId;
    private Integer inventoryId;
    private Integer bloodType;
    private Integer bloodComponent;
    private Integer quantityAllocated;
}
