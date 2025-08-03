package com.example.blooddonationsupportsystem.dtos.responses.extraction;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ExtractionResponse {
    private Integer extractionId;
    private Integer bloodTypeId;
    private String bloodTypeName;
    private Integer bloodComponentId;
    private String bloodComponentName;
    private Integer totalVolumeExtraction;
    private String notes;
    private List<ExtractionDetailResponse> details;
    private LocalDateTime extractedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}