package com.example.blooddonationsupportsystem.dtos.request.bloodRequestInventory;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BloodRequestInventoryRequest {

    @NotNull(message = "Blood request ID is required")
    @Schema(description = "ID of the blood request", example = "1")
    private Integer bloodRequestId;

    @NotNull(message = "Inventory ID is required")
    @Schema(description = "ID of the inventory to allocate from", example = "5")
    private Integer inventoryId;

    @NotNull(message = "Quantity to allocate is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    @Schema(description = "Quantity of blood to allocate", example = "2")
    private Integer quantity;
}