package com.example.blooddonationsupportsystem.dtos.responses.inventory;

import com.example.blooddonationsupportsystem.utils.InventoryStatus;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class InventoryResponse {
    private Integer id;
    private Integer bloodTypeId;
    private Integer componentId;
    private Integer quantity;
    private LocalDateTime lastUpdated;
    private LocalDate addedDate;
    private LocalDate expiryDate;
    private String batchNumber;
    private InventoryStatus status;
}
