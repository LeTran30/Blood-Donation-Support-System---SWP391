package com.example.blooddonationsupportsystem.controller;

import com.example.blooddonationsupportsystem.dtos.request.eligibilityCheck.EligibilityCheckRequest;
import com.example.blooddonationsupportsystem.dtos.responses.ResponseObject;
import com.example.blooddonationsupportsystem.service.eligibilityCheck.IEligibilityCheckService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/eligibility-check")
@CrossOrigin("*")
@RequiredArgsConstructor
public class EligibilityCheckController {
    private final IEligibilityCheckService eligibilityCheckService;

    @PostMapping()
    public ResponseEntity<?> createEligibilityCheck(
            @Valid @RequestBody EligibilityCheckRequest eligibilityCheckRequest,
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
        eligibilityCheckService.createEligibilityCheck(eligibilityCheckRequest);
        return ResponseEntity.ok(
                ResponseObject.builder()
                        .status(HttpStatus.OK)
                        .message("Successfully updated eligibility check")
                        .build()
        );
    }

    @GetMapping("/{userId}/user")
    public ResponseEntity<?> getEligibilityCheckByUserId(
            @PathVariable("userId") Integer userId
    ) {
        return ResponseEntity.ok(
                ResponseObject.builder()
                        .status(HttpStatus.OK)
                        .message("Successfully get eligibility check by user id")
                        .data(eligibilityCheckService.getEligibilityCheckByUserId(userId))
                        .build()
        );
    }
}
