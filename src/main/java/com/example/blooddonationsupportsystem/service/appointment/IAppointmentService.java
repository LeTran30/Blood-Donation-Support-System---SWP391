package com.example.blooddonationsupportsystem.service.appointment;

import com.example.blooddonationsupportsystem.dtos.request.appointment.AppointmentRequest;
import com.example.blooddonationsupportsystem.dtos.responses.appointment.AppointmentResponse;
import com.example.blooddonationsupportsystem.dtos.responses.appointment.ListAppointmentResponse;
import com.example.blooddonationsupportsystem.utils.AppointmentStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

public interface IAppointmentService {

    ResponseEntity<AppointmentResponse> createAppointment(Integer userId, AppointmentRequest appointmentRequest);

    ResponseEntity<AppointmentResponse> getAppointmentById(Integer appointmentId);

    ResponseEntity<ListAppointmentResponse> getAppointmentsByUserId(Integer userId);

    ResponseEntity<AppointmentResponse> updateAppointmentStatus(Integer appointmentId, AppointmentStatus status);

    ResponseEntity<AppointmentResponse> cancelAppointment(Integer appointmentId);

    ResponseEntity<ListAppointmentResponse> getUpcomingAppointments(LocalDateTime fromDateTime);
}
