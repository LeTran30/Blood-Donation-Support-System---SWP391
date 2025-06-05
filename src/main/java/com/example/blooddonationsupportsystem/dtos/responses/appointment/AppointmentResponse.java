package com.example.blooddonationsupportsystem.dtos.responses.appointment;

import com.example.blooddonationsupportsystem.utils.AppointmentStatus;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class AppointmentResponse {
    private Integer appointmentId;
    private Integer userId;
    private LocalDateTime appointmentDate;
    private AppointmentStatus status; // Scheduled, Completed, Cancelled
    private String message;
}
