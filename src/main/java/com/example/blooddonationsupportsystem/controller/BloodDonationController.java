package com.example.blooddonationsupportsystem.controller;

import com.example.blooddonationsupportsystem.dtos.request.bloodDonation.BloodDonationRequest;
import com.example.blooddonationsupportsystem.dtos.responses.ResponseObject;
import com.example.blooddonationsupportsystem.dtos.responses.bloodDonation.BloodDonationResponse;
import com.example.blooddonationsupportsystem.models.BloodDonation;
import com.example.blooddonationsupportsystem.repositories.UserRepository;
import com.example.blooddonationsupportsystem.service.bloodDonation.IBloodDonationService;
import com.example.blooddonationsupportsystem.service.inventory.IInventoryService;
import com.example.blooddonationsupportsystem.utils.DonationStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/blood-donation")
@RequiredArgsConstructor
@CrossOrigin("*")
public class BloodDonationController {
    private final IBloodDonationService bloodDonationService;
    private final IInventoryService inventoryService;

    @GetMapping()
    public ResponseEntity<?> getBloodDonation(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<BloodDonationResponse> bloodDonations = bloodDonationService.getAllBloodDonations(page, size);
        return ResponseEntity.ok(
                ResponseObject.builder()
                        .status(HttpStatus.OK)
                        .message("Successfully get all inventory")
                        .data(bloodDonations)
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getBloodDonation(
            @PathVariable("id") Integer id
    ) {
        BloodDonationResponse bloodDonations = bloodDonationService.getBloodDonationById(id);
        return ResponseEntity.ok(
                ResponseObject.builder()
                        .status(HttpStatus.OK)
                        .message("Successfully get inventory")
                        .data(bloodDonations)
                        .build()
        );
    }

    @PostMapping()
    public ResponseEntity<?> addBloodDonation(
            @Valid @RequestBody BloodDonationRequest bloodDonationRequest,
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
                            .message(String.join(", ", errorMessages))
                            .build()
            );
        }

        try {
            BloodDonation bloodDonation = bloodDonationService.addBloodDonation(bloodDonationRequest);
            inventoryService.updateInventoryAfterDonation(bloodDonation);
            return ResponseEntity.ok(
                    ResponseObject.builder()
                            .status(HttpStatus.OK)
                            .message("Successfully created blood donation")
                            .build()
            );
        }catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    ResponseObject.builder()
                            .status(HttpStatus.CONFLICT)
                            .message("Duplicate blood donation entry detected: " + Objects.requireNonNull(e.getRootCause()).getMessage())
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ResponseObject.builder()
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .message("Error: " + e.getMessage())
                            .build()
            );
        }
    }

    @PutMapping("/donations/{id}/status")
    public ResponseEntity<BloodDonationResponse> updateStatus(
            @PathVariable int id,
            @RequestParam DonationStatus status
    ) {
        BloodDonation updated = bloodDonationService.updateDonationStatus(id, status);
        BloodDonationResponse response = BloodDonationResponse.builder()
                .donationId(updated.getDonationId())
                .user(updated.getUser().getId())
                .bloodType(updated.getBloodType().getBloodTypeId())
                .donationDate(updated.getDonationDate())
                .volumeMl(updated.getVolumeMl())
                .status(updated.getStatus())
                .healthCheck(updated.getHealthCheck().getId())
                .build();
        return ResponseEntity.ok(response);
    }
}
