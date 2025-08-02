package com.example.blooddonationsupportsystem.dtos.responses.extract;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ExtractResponse {
    private Integer extractId;
    private Integer inventoryId; // Can be null
    private Integer bloodTypeId;
    private String bloodTypeName;
    private Integer bloodComponentId;
    private String bloodComponentName;
    private Integer volumeExtracted;
    private LocalDateTime extractedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}