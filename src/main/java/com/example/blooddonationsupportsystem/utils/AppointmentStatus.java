package com.example.blooddonationsupportsystem.utils;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(enumAsRef = true)
public enum AppointmentStatus {
    PENDING,
    SCHEDULED,
    COMPLETED,
    CANCELLED,
    MEDICAL_COMPLETED
}
