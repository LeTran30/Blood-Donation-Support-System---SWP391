package com.example.blooddonationsupportsystem.dtos.request.inventory;


import jakarta.validation.constraints.Max;
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
    private Integer bloodTypeId;

    @NotNull
    private Integer bloodComponentId;


    @Min(value = 1, message = "quantity must be greater than or equal to 0")
    @Max(value = 1000, message = "quantity must be less than or equal to 1000")
    private Integer quantity;

}
