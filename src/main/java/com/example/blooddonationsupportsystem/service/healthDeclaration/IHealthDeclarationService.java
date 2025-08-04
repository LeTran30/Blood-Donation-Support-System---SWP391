package com.example.blooddonationsupportsystem.service.healthDeclaration;

import com.example.blooddonationsupportsystem.dtos.request.healthDeclaration.HealthDeclarationRequest;
import com.example.blooddonationsupportsystem.utils.Role;
import org.springframework.http.ResponseEntity;

public interface IHealthDeclarationService {
    ResponseEntity<?> createHealthDeclaration(HealthDeclarationRequest request, Integer userId);

    ResponseEntity<?> getHealthDeclarationByAppointmentId(Integer appointmentId);

    ResponseEntity<?> getHealthDeclarationsByUserId(Integer userId, int page, int size);

    ResponseEntity<?> updateHealthDeclaration(Integer healthDeclarationId, HealthDeclarationRequest request, Integer userId, Role role);

    ResponseEntity<?> getHealthDeclarationById(Integer id);

    ResponseEntity<?> deleteHealthDeclaration(Integer healthDeclarationId);
}
