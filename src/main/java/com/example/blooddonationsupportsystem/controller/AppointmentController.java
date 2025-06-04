package com.example.blooddonationsupportsystem.controller;

import com.example.blooddonationsupportsystem.dtos.responses.appointment.AppointmentResponse;
import com.example.blooddonationsupportsystem.dtos.responses.appointment.ListAppointmentResponse;
import com.example.blooddonationsupportsystem.models.User;
import com.example.blooddonationsupportsystem.repositories.UserRepository;
import com.example.blooddonationsupportsystem.service.appointment.IAppointmentService;
import com.example.blooddonationsupportsystem.utils.AppointmentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/appointment")
@RequiredArgsConstructor
@CrossOrigin("*")
public class AppointmentController {
    private final IAppointmentService appointmentService;
    private final UserRepository userRepository;

    @PostMapping
    @PreAuthorize("hasAuthority('member:create')")
    public ResponseEntity<?> createAppointment(
            @RequestParam("appointmentDate")
            @DateTimeFormat(pattern = "HH:mm dd/MM/yyyy") LocalDateTime appointmentDate,
            Principal principal
    ) {
        String email = principal.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return appointmentService.createAppointment(user.getId(), appointmentDate);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponse> getAppointmentById(@PathVariable("id") Integer appointmentId) {
        return appointmentService.getAppointmentById(appointmentId);
    }

    @GetMapping("/user/")
    public ResponseEntity<ListAppointmentResponse> getAppointmentsByUserId(Principal principal) {
        String email = principal.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return appointmentService.getAppointmentsByUserId(user.getId());
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('staff:update')")
    public ResponseEntity<AppointmentResponse> updateStatus(
            @PathVariable("id") Integer appointmentId,
            @RequestParam AppointmentStatus status) {
        return appointmentService.updateAppointmentStatus(appointmentId, status);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('member:update')")
    public ResponseEntity<AppointmentResponse> cancelAppointment(@PathVariable("id") Integer appointmentId) {
        return appointmentService.cancelAppointment(appointmentId);
    }

    @GetMapping("/upcoming")
    @PreAuthorize("hasAuthority('member:read')")
    public ResponseEntity<ListAppointmentResponse> getUpcomingAppointments(
            @RequestParam @DateTimeFormat(pattern = "HH:mm dd/MM/yyyy") LocalDateTime from) {
        return appointmentService.getUpcomingAppointments(from);
    }
}
