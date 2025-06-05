package com.example.blooddonationsupportsystem.dtos.responses.distanceSearch;

import lombok.*;

import java.sql.Timestamp;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DistanceSearchResponse {
    private Integer searchId;
    private Integer userId;
    private Integer targetUserId;
    private Integer bloodTypeId;
    private Double distanceKM;
    private Timestamp searchTime;
}