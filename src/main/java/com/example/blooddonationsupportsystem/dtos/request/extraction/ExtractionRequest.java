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

    @NotNull(message = "ID nhóm máu không được để trống")
    private Integer bloodTypeId;
    
    @NotNull(message = "ID thành phần máu không được để trống")
    private Integer bloodComponentId;
    
    @NotNull(message = "Thể tích máu trích xuất không được để trống")
    @Min(value = 250, message = "Thể tích máu trích xuất tối thiểu là 250ml")
    private Integer totalVolumeExtraction;

    private String notes;

    @NotNull
    private LocalDateTime extractedAt; // If null, current time will be used
}