package com.example.blooddonationsupportsystem.dtos.responses.bloodRequest;

import com.example.blooddonationsupportsystem.models.BloodComponent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BloodRequestResponse {
    private Integer requestId;
    private BloodComponent component;
    private String bloodType;
    private String urgencyLevel;
    private String status;
    private String createdAt;
    private Integer userId;
}