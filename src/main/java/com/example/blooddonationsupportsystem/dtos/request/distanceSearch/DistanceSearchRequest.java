package com.example.blooddonationsupportsystem.dtos.request.distanceSearch;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DistanceSearchRequest {
    @NotNull(message = "User ID must not be null")
    @Positive(message = "User ID must be a positive integer")
    private Integer userId;

    @NotNull(message = "Blood Type ID must not be null")
    @Positive(message = "Blood Type ID must be a positive integer")
    private Integer bloodTypeId;

    @NotNull(message = "Target User ID must not be null")
    @Positive(message = "Target User ID must be a positive integer")
    private Integer targetUserId;

    @NotNull(message = "DistanceKM must not be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "DistanceKM must be greater than 0")
    private Double distanceKM;

}