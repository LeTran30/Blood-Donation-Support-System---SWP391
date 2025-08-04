package com.example.blooddonationsupportsystem.dtos.request.inventory;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Positive;
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

    @NotNull
    private Integer componentId;

    @Positive(value = 250, message = "Thể tích là số nguyên dương")
    private Integer quantity;
    @NotNull
    private LocalDate addedDate;
    
    @NotNull
    @Future(message = "Ngày quá hạn là ngày trong tương lai")
    private LocalDate expiryDate;

    @NotNull(message = "Cần cung cấp mã lô")
    private String batchNumber;
}
