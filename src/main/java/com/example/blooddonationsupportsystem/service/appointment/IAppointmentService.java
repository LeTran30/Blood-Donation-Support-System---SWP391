package com.example.blooddonationsupportsystem.service.appointment;

import com.example.blooddonationsupportsystem.dtos.request.appointment.AppointmentRequest;

import com.example.blooddonationsupportsystem.utils.AppointmentStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

public interface IAppointmentService {

    ResponseEntity<?> createAppointment(Integer userId, AppointmentRequest appointmentRequest);

    ResponseEntity<?> getAppointmentById(Integer appointmentId);

    ResponseEntity<?> getAppointmentsByUserId(Integer userId);

    ResponseEntity<?> updateAppointmentStatus(Integer appointmentId, AppointmentStatus status);

    ResponseEntity<?> cancelAppointment(Integer appointmentId);

    ResponseEntity<?> getAppointmentsWithFilters(LocalDateTime fromDateTime, LocalDateTime toDateTime, AppointmentStatus status, Integer userId);
}
