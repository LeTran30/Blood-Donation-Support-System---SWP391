package com.example.blooddonationsupportsystem.controller;

import com.example.blooddonationsupportsystem.dtos.request.certificate.CertificateRequest;
import com.example.blooddonationsupportsystem.dtos.responses.ResponseObject;
import com.example.blooddonationsupportsystem.models.User;
import com.example.blooddonationsupportsystem.repositories.UserRepository;
import com.example.blooddonationsupportsystem.service.certificate.ICertificateService;
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
@RequestMapping("/api/v1/certificates")
@RequiredArgsConstructor
@CrossOrigin("*")
public class CertificateController {
    private final ICertificateService certificateService;
    private final UserRepository userRepository;

    @PostMapping("/generate/{bloodDonationInforId}")
    @PreAuthorize("hasAuthority('staff:create')")
    public ResponseEntity<?> generateCertificate(@PathVariable Integer bloodDonationInforId) {
        return certificateService.generateCertificateForDonation(bloodDonationInforId);
    }

    @GetMapping("/{certificateId}")
    @PreAuthorize("hasAnyAuthority('staff:read', 'member:read')")
    public ResponseEntity<?> getCertificateById(@PathVariable Integer certificateId) {
        return certificateService.getCertificateById(certificateId);
    }

    @GetMapping("/user")
    @PreAuthorize("hasAnyAuthority('staff:read', 'member:read')")
    public ResponseEntity<?> getCertificatesByUserId(
            @RequestParam(required = false) Integer userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Principal principal
    ) {
        String email = principal.getName();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (userId == null || currentUser.getRole() == Role.MEMBER) {
            return certificateService.getCertificatesByUserId(currentUser.getId(), page, size);
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
            return certificateService.getCertificatesByUserId(userId, page, size);
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('staff:update')")
    public ResponseEntity<?> updateCertificate(
            @PathVariable Integer id,
            @RequestBody CertificateRequest request,
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
        return certificateService.updateCertificate(id, request.getDescription(), request.getCertificateType());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('staff:delete')")
    public ResponseEntity<?> deleteCertificate(@PathVariable Integer id) {
        return certificateService.deleteCertificate(id);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('staff:read')")
    public ResponseEntity<?> getAllCertificates(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return certificateService.getAllCertificates(page, size);
    }


}
