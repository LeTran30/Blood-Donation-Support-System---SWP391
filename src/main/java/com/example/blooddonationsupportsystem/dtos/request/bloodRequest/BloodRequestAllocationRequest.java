package com.example.blooddonationsupportsystem.dtos.request.bloodRequest;

import com.example.blooddonationsupportsystem.utils.UrgencyLevel;
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

    @Schema(description = "Urgency level of the request", example = "HIGH")
    @NotNull(message = "Urgency level must not be null")
    private UrgencyLevel urgencyLevel;

    @NotNull(message = "Quantity id must not be null")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
}
