package com.example.blooddonationsupportsystem.controller;

import com.example.blooddonationsupportsystem.dtos.request.bloodRequest.BloodRequestAllocationRequest;
import com.example.blooddonationsupportsystem.dtos.responses.ResponseObject;
import com.example.blooddonationsupportsystem.service.bloodRequest.IBloodRequestService;
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
@RequestMapping("/api/blood-requests")
@RequiredArgsConstructor
@CrossOrigin("*")
public class BloodRequestController {
    private final IBloodRequestService bloodRequestService;

    @PostMapping
    @PreAuthorize("true")
    public ResponseEntity<?> createBloodRequest(
            @Valid @RequestBody BloodRequestAllocationRequest request,
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
    @PreAuthorize("true")
    public ResponseEntity<?> getAllBloodRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        return bloodRequestService.getAllRequests(page, size);
    }

    @GetMapping("/{requestId}/inventory")
    @PreAuthorize("true")
    public ResponseEntity<?> getInventoryForRequest(
            @PathVariable Integer requestId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return bloodRequestService.getInventoryForRequest(requestId, page, size);
    }

    @PutMapping("/{requestId}/allocate")
    @PreAuthorize("true")
    public ResponseEntity<?> allocateInventoryToRequest(
            @PathVariable Integer requestId
    ) {

        return bloodRequestService.allocateInventory(requestId);
    }

    @DeleteMapping("/{requestId}")
    @PreAuthorize("true")
    public ResponseEntity<?> deleteBloodRequest(@PathVariable Integer requestId) {
        return bloodRequestService.deleteRequest(requestId);
    }

}
