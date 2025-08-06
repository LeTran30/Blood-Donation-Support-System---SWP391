package com.example.blooddonationsupportsystem.controller;

import com.example.blooddonationsupportsystem.dtos.request.healthDeclaration.HealthDeclarationRequest;
import com.example.blooddonationsupportsystem.dtos.request.healthDeclaration.HealthDeclarationUpdateRequest;
import com.example.blooddonationsupportsystem.dtos.responses.ResponseObject;
import com.example.blooddonationsupportsystem.models.User;
import com.example.blooddonationsupportsystem.repositories.UserRepository;
import com.example.blooddonationsupportsystem.service.healthDeclaration.IHealthDeclarationService;
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
import java.util.List;

@RestController
@RequestMapping("/api/v1/health-declaration")
@RequiredArgsConstructor
@CrossOrigin("*")
public class HealthDeclarationController {
    private final IHealthDeclarationService healthDeclarationService;
    private final UserRepository userRepository;

    @GetMapping("/appointment/{appointmentId}")
    @PreAuthorize("hasAnyAuthority('staff:read', 'member:read')")
    public ResponseEntity<?> getHealthDeclarationByAppointmentId(
            @PathVariable Integer appointmentId
    ) {
        return healthDeclarationService.getHealthDeclarationByAppointmentId(appointmentId);
    }

    @GetMapping("/user")
    @PreAuthorize("hasAnyAuthority('staff:read', 'member:read')")
    public ResponseEntity<?> getHealthDeclarationsByUserId(
            @RequestParam(required = false) Integer userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Principal principal
    ) {
        String email = principal.getName();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (userId == null || currentUser.getRole() == Role.MEMBER) {
            return healthDeclarationService.getHealthDeclarationsByUserId(currentUser.getId(), page, size);
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
            return healthDeclarationService.getHealthDeclarationsByUserId(userId, page, size);
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('member:create', 'staff:create')")
    public ResponseEntity<?> createHealthDeclaration(
            @Valid @RequestBody HealthDeclarationRequest request,
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
        return healthDeclarationService.createHealthDeclaration(request, user.getId());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('member:update', 'staff:update')")
    public ResponseEntity<?> updateHealthDeclaration(
            @PathVariable("id") Integer id,
            @RequestBody HealthDeclarationUpdateRequest request,
            Principal principal
    ) {
        String email = principal.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return healthDeclarationService.updateHealthDeclaration(id, request, user.getId(), user.getRole());
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('member:delete', 'staff:delete')")
    public ResponseEntity<?> deleteHealthDeclaration(@PathVariable("id") Integer id) {
        return healthDeclarationService.deleteHealthDeclaration(id);
    }
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('member:read', 'staff:read')")
    public ResponseEntity<?> getHealthDeclarationById(@PathVariable("id") Integer id) {
        return healthDeclarationService.getHealthDeclarationById(id);
    }

}