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
public class InventoryUpdateRequest {
    @NotNull
    private Integer bloodTypeId;

    @NotNull
    private Integer componentId;

    @Min(value = 250, message = "Thể tích tối thiểu là 250ml")
    @Max(value = 1000, message = "Thể tích tối đa là 250ml")
    private Integer quantity;

    @NotNull
    private LocalDate addedDate;

    @NotNull
    @FutureOrPresent(message = "Ngày quá hạn là ngày trong tương lai")
    private LocalDate expiryDate;

}
