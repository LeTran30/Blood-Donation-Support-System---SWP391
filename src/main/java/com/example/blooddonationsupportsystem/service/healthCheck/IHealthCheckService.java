package com.example.blooddonationsupportsystem.service.healthCheck;

import com.example.blooddonationsupportsystem.dtos.request.healthCheck.HealthCheckRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;

public interface IHealthCheckService {
    ResponseEntity<?> getByAppointmentId(Integer appointmentId);

    ResponseEntity<?> createHealthCheck(Integer appointmentId, @Valid HealthCheckRequest request);

    ResponseEntity<?> getHealthChecksByUserId(Integer userId, int page, int size);
}
