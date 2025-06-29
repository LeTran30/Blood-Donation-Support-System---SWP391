package com.example.blooddonationsupportsystem.controller;

import com.example.blooddonationsupportsystem.dtos.request.appointment.AppointmentRequest;
import com.example.blooddonationsupportsystem.dtos.request.healthCheck.HealthCheckRequest;
import com.example.blooddonationsupportsystem.dtos.responses.ResponseObject;
import com.example.blooddonationsupportsystem.models.User;
import com.example.blooddonationsupportsystem.repositories.UserRepository;
import com.example.blooddonationsupportsystem.service.appointment.IAppointmentService;
import com.example.blooddonationsupportsystem.service.healthCheck.HealthCheckService;
import com.example.blooddonationsupportsystem.utils.AppointmentStatus;
import com.example.blooddonationsupportsystem.utils.Role;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/appointment")
@RequiredArgsConstructor
@CrossOrigin("*")
public class AppointmentController {
    private final IAppointmentService appointmentService;
    private final UserRepository userRepository;
    private final HealthCheckService healthCheckService;

    @PostMapping
    @PreAuthorize("hasAuthority('member:create')")
    public ResponseEntity<?> createAppointment(
            @Valid @RequestBody AppointmentRequest appointmentRequest,
            BindingResult result,
            Principal principal
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
        String email = principal.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return appointmentService.createAppointment(user.getId(), appointmentRequest);
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('member:read', 'staff:read')")
    public ResponseEntity<?> getAppointmentById(@PathVariable("id") Integer appointmentId) {
        return appointmentService.getAppointmentById(appointmentId);
    }

    @GetMapping("/user")
    @PreAuthorize("hasAnyAuthority('member:read', 'staff:read')")
    public ResponseEntity<?> getAppointmentsByUser(
            @RequestParam(value = "userId", required = false) Integer userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Principal principal
    ) {
        String email = principal.getName();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (userId == null || currentUser.getRole() == Role.MEMBER) {
            return appointmentService.getAppointmentsByUserId(currentUser.getId(), page, size);
        }

        if (currentUser.getRole() == Role.STAFF) {
            boolean exists = userRepository.existsById(userId);
            if (!exists) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ResponseObject.builder()
                                .status(HttpStatus.NOT_FOUND)
                                .message("User with id " + userId + " not found")
                                .build());
            }
            return appointmentService.getAppointmentsByUserId(userId, page, size);
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }


    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('staff:update')")
    public ResponseEntity<?> updateStatus(
            @PathVariable("id") Integer appointmentId,
            @RequestParam String status) {
        AppointmentStatus appointmentStatus;
        try {
            appointmentStatus = AppointmentStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ResponseObject.builder()
                            .status(HttpStatus.BAD_REQUEST)
                            .message("Invalid AppointmentStatus value: " + status)
                            .build());
        }
        return appointmentService.updateAppointmentStatus(appointmentId, appointmentStatus);
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('member:delete')")
    public ResponseEntity<?> cancelAppointment(@PathVariable("id") Integer appointmentId) {
        return appointmentService.cancelAppointment(appointmentId);
    }

    @GetMapping("/filter")
    @PreAuthorize("hasAuthority('staff:read')")
    public ResponseEntity<?> getAppointmentsWithFilters(
            @RequestParam(required = false) LocalDateTime from,
            @RequestParam(required = false) LocalDateTime to,
            @RequestParam(required = false) AppointmentStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer userId
    ) {
        return appointmentService.getAppointmentsWithFilters(from, to, status, userId, page, size);
    }
}
