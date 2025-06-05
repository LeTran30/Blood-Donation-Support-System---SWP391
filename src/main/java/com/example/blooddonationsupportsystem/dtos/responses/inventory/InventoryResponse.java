package com.example.blooddonationsupportsystem.dtos.responses.inventory;

import com.example.blooddonationsupportsystem.models.BloodType;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class InventoryResponse {
    private Long id;
    private BloodType bloodType;
    private Integer quantity;
    private LocalDateTime lastUpdated;
}
