package com.example.blooddonationsupportsystem.dtos.request.appointment;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

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