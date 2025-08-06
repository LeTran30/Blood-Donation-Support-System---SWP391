package com.example.blooddonationsupportsystem.dtos.request.donorSearch;

import lombok.Data;

@Data
public class DonorSearchRequest {
    private Integer bloodTypeId;
    private Double latitude;
    private Double longitude;
}
