package com.example.blooddonationsupportsystem.dtos.request.inventory;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class InventoryRequest {
    @NotNull
    private Integer bloodTypeId;

    @Min(value = 1, message = "quantity must be greater than or equal to 1")
    @Max(value = 1000, message = "quantity must be less than or equal to 1000")
    private Integer quantity;
    
    @NotNull
    private LocalDate addedDate;
    
    @NotNull
    @FutureOrPresent(message = "Expiry date must be in the present or future")
    private LocalDate expiryDate;
}
