package com.example.blooddonationsupportsystem.service.healthDeclaration;

import com.example.blooddonationsupportsystem.dtos.request.healthDeclaration.HealthDeclarationRequest;
import org.springframework.http.ResponseEntity;

public interface IHealthDeclarationService {
    ResponseEntity<?> createHealthDeclaration(HealthDeclarationRequest request);

    ResponseEntity<?> getHealthDeclarationByAppointmentId(Integer appointmentId);

    ResponseEntity<?> getHealthDeclarationsByUserId(Integer userId, int page, int size);

    ResponseEntity<?> updateHealthDeclaration(Integer healthDeclarationId, HealthDeclarationRequest request);

    ResponseEntity<?> getHealthDeclarationById(Integer id);

    ResponseEntity<?> deleteHealthDeclaration(Integer healthDeclarationId);
}
