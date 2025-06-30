package com.example.blooddonationsupportsystem.dtos.responses.bloodRequest;

import com.example.blooddonationsupportsystem.utils.UrgencyLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BloodRequestResponse {
    private Integer requestId;
    private Integer componentId;
    private Integer bloodTypeId;
    private UrgencyLevel urgencyLevel;
    private String status;
    private Integer quantity;
    private String createdAt;
    private Integer createdBy;

}