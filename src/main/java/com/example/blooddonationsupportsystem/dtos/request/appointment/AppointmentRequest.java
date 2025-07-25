package com.example.blooddonationsupportsystem.dtos.request.appointment;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentRequest {

    @NotNull(message = "Appointment date is required")
    @Schema(description = "Appointment date and time",
            example = "2025-06-05T18:00:00")
    private LocalDateTime appointmentDate;

}