package com.example.blooddonationsupportsystem.controller;

import com.example.blooddonationsupportsystem.dtos.request.bloodDonation.BloodDonationRequest;
import com.example.blooddonationsupportsystem.dtos.responses.ResponseObject;
import com.example.blooddonationsupportsystem.dtos.responses.bloodDonation.BloodDonationResponse;
import com.example.blooddonationsupportsystem.service.bloodDonation.IBloodDonationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/blood-donation")
@RequiredArgsConstructor
@CrossOrigin("*")
public class BloodDonationController {
    private final IBloodDonationService bloodDonationService;

    @GetMapping()
    public ResponseEntity<?> getBloodDonation() {
        List<BloodDonationResponse> bloodDonations = bloodDonationService.getAllBloodDonations();
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
                            .message(String.valueOf(errorMessages))
                            .build()
            );
        }
        bloodDonationService.addBloodDonation(bloodDonationRequest);
        return ResponseEntity.ok(
                ResponseObject.builder()
                        .status(HttpStatus.OK)
                        .message("Successfully created blood donation")
                        .build()
        );
    }
}
