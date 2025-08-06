package com.example.blooddonationsupportsystem.dtos.request.extraction;

import jakarta.validation.constraints.Positive;
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
    @Positive(message = "Thể tích máu trích xuất là một số nguyên dương")
    private Integer totalVolumeExtraction;

    private String notes;

    @NotNull
    private LocalDateTime extractedAt; // If null, current time will be used
}
