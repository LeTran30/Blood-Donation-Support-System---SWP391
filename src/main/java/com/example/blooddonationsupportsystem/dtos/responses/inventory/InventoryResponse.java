package com.example.blooddonationsupportsystem.dtos.responses.inventory;

import lombok.*;

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
    private Integer bloodComponentId;
    private Integer quantity;
    private LocalDateTime lastUpdated;
}
