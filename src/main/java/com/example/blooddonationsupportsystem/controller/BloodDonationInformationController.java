package com.example.blooddonationsupportsystem.controller;

import com.example.blooddonationsupportsystem.dtos.request.bloodDonationInfo.BloodDonationInformationRequest;
import com.example.blooddonationsupportsystem.dtos.responses.ResponseObject;
import com.example.blooddonationsupportsystem.models.User;
import com.example.blooddonationsupportsystem.repositories.UserRepository;
import com.example.blooddonationsupportsystem.service.bloodDonationInformation.IBloodDonationInformationService;
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
@RequestMapping("/api/v1/blood-donation-infor")
@RequiredArgsConstructor
@CrossOrigin("*")
public class BloodDonationInformationController {
    private final IBloodDonationInformationService bloodDonationInforService;
    private final UserRepository userRepository;

    @PostMapping
    @PreAuthorize("hasAuthority('staff:create')")
    public ResponseEntity<?> createBloodDonationInfor(
            @Valid @RequestBody BloodDonationInformationRequest request,
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
        return bloodDonationInforService.createBloodDonationInformation(request);
    }

    @GetMapping("/appointment/{appointmentId}")
    @PreAuthorize("hasAnyAuthority('staff:read', 'member:read')")
    public ResponseEntity<?> getBloodDonationInforByAppointmentId(
            @PathVariable Integer appointmentId
            ) {

        return bloodDonationInforService.getBloodDonationInformationByAppointmentId(appointmentId);
    }

    @GetMapping("/user")
    @PreAuthorize("hasAnyAuthority('staff:read', 'member:read')")
    public ResponseEntity<?> getBloodDonationInforsByUserId(
            @RequestParam(required = false) Integer userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Principal principal
    ) {
        String email = principal.getName();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (userId == null || currentUser.getRole() == Role.MEMBER) {
            return bloodDonationInforService.getBloodDonationInformationsByUserId(currentUser.getId(), page, size);
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
            return bloodDonationInforService.getBloodDonationInformationsByUserId(userId, page, size);
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @GetMapping("/user/total-volume")
    @PreAuthorize("hasAnyAuthority('staff:read', 'member:read')")
    public ResponseEntity<?> getTotalBloodVolumeByUser(
            @RequestParam(required = false) Integer userId,
            Principal principal
    ) {
        String email = principal.getName();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (userId == null || currentUser.getRole() == Role.MEMBER) {
            return bloodDonationInforService.getTotalBloodVolumeByUser(currentUser.getId());
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
            return bloodDonationInforService.getTotalBloodVolumeByUser(userId);
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('staff:update')")
    public ResponseEntity<?> updateBloodDonationInformation(
            @PathVariable Integer id,
            @RequestBody BloodDonationInformationRequest request
    ) {
        return bloodDonationInforService.updateBloodDonationInformation(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('staff:delete')")
    public ResponseEntity<?> deleteBloodDonationInformation(@PathVariable Integer id) {
        return bloodDonationInforService.deleteBloodDonationInformation(id);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('staff:read')")
    public ResponseEntity<?> getAllBloodDonationInformations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return bloodDonationInforService.getAllBloodDonationInformations(page, size);
    }

}