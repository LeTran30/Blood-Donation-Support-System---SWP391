package com.example.blooddonationsupportsystem.dtos.responses.donorSearch;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DonorSearchResponse {
    private Integer userId;
    private String fullName;
    private String email;
    private Double latitude;
    private Double longitude;
    private double distanceKM;
}
