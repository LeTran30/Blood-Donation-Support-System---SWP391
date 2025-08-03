package com.example.blooddonationsupportsystem.utils;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(enumAsRef = true)
public enum UrgencyLevel {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}
