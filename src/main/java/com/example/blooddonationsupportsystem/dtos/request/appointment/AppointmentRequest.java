package com.example.blooddonationsupportsystem.dtos.request.appointment;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm dd/MM/yyyy")
    @Schema(description = "Appointment date and time in the format HH:mm dd/MM/yyyy, e.g. 15:30 25/06/2025",
            example = "15:30 25/06/2025")
    private LocalDateTime appointmentDate;

}