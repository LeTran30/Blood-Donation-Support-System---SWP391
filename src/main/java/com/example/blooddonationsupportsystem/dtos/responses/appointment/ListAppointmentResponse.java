package com.example.blooddonationsupportsystem.dtos.responses.appointment;

import lombok.*;

import java.util.List;
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ListAppointmentResponse {
    private String message;
    List<AppointmentResponse> appointments;
}