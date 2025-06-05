package com.example.blooddonationsupportsystem.controller;

import com.example.blooddonationsupportsystem.dtos.request.bloodrequest.BloodRequestRequest;
import com.example.blooddonationsupportsystem.dtos.responses.ResponseObject;
import com.example.blooddonationsupportsystem.service.bloodRequest.IBloodRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/blood-requests")
@RequiredArgsConstructor
@CrossOrigin("*")
public class BloodRequestController {
    private final IBloodRequestService bloodRequestService;

    @PostMapping
    public ResponseEntity<?> createBloodRequest(
            @Valid @RequestBody BloodRequestRequest request,
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
        return bloodRequestService.createRequest(request);
    }

    @GetMapping
    public ResponseEntity<?> getAllBloodRequests() {
        return bloodRequestService.getAllRequests();
    }

    @GetMapping("/{requestId}/inventory")
    public ResponseEntity<?> getInventoryForRequest(@PathVariable Integer requestId) {
        return bloodRequestService.getInventoryForRequest(requestId);
    }
}
