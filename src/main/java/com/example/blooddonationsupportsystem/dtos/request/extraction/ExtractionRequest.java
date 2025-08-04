package com.example.blooddonationsupportsystem.dtos.request.extraction;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ExtractionRequest {

    @NotNull(message = "Blood type ID is required")
    private Integer bloodTypeId;
    
    @NotNull(message = "Blood component ID is required")
    private Integer bloodComponentId;
    
    @NotNull(message = "Volume extracted is required")
    @Min(value = 1, message = "Volume extracted must be greater than 0")
    private Integer totalVolumeExtraction;

    private String notes;

    @NotNull
    private LocalDateTime extractedAt; // If null, current time will be used
}