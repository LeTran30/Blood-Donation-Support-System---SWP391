package com.example.blooddonationsupportsystem.dtos.request.bloodRequest;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Setter
@Getter
@Data
public class BloodRequestAllocationRequest {

    @NotNull(message = "Blood type id must not be null")
    private Integer bloodTypeId;

    @NotNull(message = "Blood component id must not be null")
    private Integer bloodComponentId;

    @NotNull(message = "Urgency Level id must not be null")
    private String UrgencyLevel;

    @Schema(
            description = "Map of Inventory ID to Quantity allocation",
            example = "{\"1\": 10, \"2\": 5}"
    )
    @NotEmpty(message = "Allocations must not be empty")
    private Map<@NotNull(message = "Inventory ID cannot be null") Integer,
            @NotNull(message = "Quantity cannot be null")
            @Min(value = 1, message = "Quantity must be at least 1") Integer> allocations;

}
