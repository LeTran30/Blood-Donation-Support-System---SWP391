package com.example.blooddonationsupportsystem.dtos.request.inventory;

import com.example.blooddonationsupportsystem.models.BloodComponent;
import com.example.blooddonationsupportsystem.models.BloodType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class InventoryRequest {
    @NotNull
    private BloodType bloodType;

    @NotNull
    private BloodComponent bloodComponent;

    @NotNull
    @Min(0)
    private Integer quantity;

}
