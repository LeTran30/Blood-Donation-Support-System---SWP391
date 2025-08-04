package com.example.blooddonationsupportsystem.controller;


import com.example.blooddonationsupportsystem.dtos.request.healthCheck.HealthCheckRequest;
import com.example.blooddonationsupportsystem.dtos.responses.ResponseObject;
import com.example.blooddonationsupportsystem.service.healthCheck.IHealthCheckService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/health-check")
@RequiredArgsConstructor
@CrossOrigin("*")
public class HealthCheckController {

    private final IHealthCheckService healthCheckService;

    @PostMapping
    public ResponseEntity<?> submitHealthCheck(
            @Valid @RequestBody HealthCheckRequest healthCheckRequest,
            BindingResult result
    ) {
        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(
                    ResponseObject.builder()
                            .status(HttpStatus.BAD_REQUEST)
                            .message(String.valueOf(errorMessages))
                            .build()
            );
        }
        return healthCheckService.createHealthCheck(healthCheckRequest);
    }

    @GetMapping("/users/{userId}/health-checks")
    @PreAuthorize("hasAnyAuthority('member:read', 'staff:read', 'admin:read')")
    public ResponseEntity<?> getHealthChecksByUser(
            @PathVariable Integer userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return healthCheckService.getHealthChecksByUserId(userId, page, size);
    }

    @GetMapping("/health-checks/{id}")
    @PreAuthorize("hasAnyAuthority('staff:read', 'admin:read')")
    public ResponseEntity<?> getHealthCheckById(@PathVariable("id") Integer id) {
        return healthCheckService.getHealthCheckById(id);
    }

    @GetMapping("/health-checks/by-appointment/{appointmentId}")
    @PreAuthorize("hasAnyAuthority('staff:read', 'admin:read')")
    public ResponseEntity<?> getHealthCheckByAppointmentId(@PathVariable("appointmentId") Integer id) {
        return healthCheckService.getByAppointmentId(id);
    }
}
