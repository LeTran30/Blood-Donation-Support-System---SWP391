package com.example.blooddonationsupportsystem.dtos.responses.distanceSearch;

import lombok.*;

import java.sql.Timestamp;

@Data
@Builder
public class DistanceSearchResponse {
    private Integer searchId;
    private Integer userId;
    private Integer targetUserId;
    private String targetUsername;
    private Integer bloodTypeId;
    private String bloodTypeName;
    private Double distanceKM;
    private Timestamp searchTime;
}