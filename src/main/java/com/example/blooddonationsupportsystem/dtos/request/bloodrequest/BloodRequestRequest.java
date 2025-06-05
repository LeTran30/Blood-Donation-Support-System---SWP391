package com.example.blooddonationsupportsystem.dtos.request.bloodrequest;

import com.example.blooddonationsupportsystem.models.BloodComponent;
import com.example.blooddonationsupportsystem.models.BloodType;
import com.example.blooddonationsupportsystem.utils.RequestStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public class BloodRequestRequest {

    private Integer requestId;

    @NotNull(message = "Blood component is required")
    private BloodComponent component;

    @NotNull(message = "Blood type is required")
    private BloodType bloodType;

    @NotNull(message = "Urgency level is required")
    @Size(min = 3, max = 20, message = "Urgency level must be between 3 and 20 characters")
    private String urgencyLevel;

    private RequestStatus status;

    private Instant createdAt;
}
