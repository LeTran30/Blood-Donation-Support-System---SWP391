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
    private Integer bloodType;

    @NotNull
    private Integer bloodComponent;

    @NotNull
    @Min(1)
    private Integer quantity;

}
