package com.example.blooddonationsupportsystem.controller;

import com.example.blooddonationsupportsystem.dtos.request.extraction.ExtractionRequest;
import com.example.blooddonationsupportsystem.dtos.responses.ResponseObject;
import com.example.blooddonationsupportsystem.service.extraction.IExtractionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/extractions")
@RequiredArgsConstructor
@CrossOrigin("*")
public class ExtractionController {

    private final IExtractionService extractionService;

    @PostMapping
    @PreAuthorize("true")
    public ResponseEntity<?> createExtraction(
            @RequestBody ExtractionRequest request,
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
        return extractionService.createExtraction(request);
    }

    @GetMapping("/{id}")
    @PreAuthorize("true")
    public ResponseEntity<?> getExtractionById(@PathVariable Integer id) {
        return extractionService.getExtractionById(id);
    }

    @GetMapping
    @PreAuthorize("true")
    public ResponseEntity<?> getAllExtractions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer bloodTypeId,
            @RequestParam(required = false) Integer bloodComponentId
    ) {
        return extractionService.getAllExtractions(page, size, bloodTypeId, bloodComponentId);
    }

    @GetMapping("/{extractionId}/details")
    @PreAuthorize("true")
    public ResponseEntity<?> getExtractionDetails(@PathVariable Integer extractionId) {
        return extractionService.getExtractionDetailsByExtractionId(extractionId);
    }

    @PutMapping("/{id}")
    @PreAuthorize("true")
    public ResponseEntity<?> updateExtraction(@PathVariable Integer id, @RequestBody ExtractionRequest request) {
        return extractionService.updateExtraction(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("true")
    public ResponseEntity<?> deleteExtraction(@PathVariable Integer id) {
        return extractionService.deleteExtraction(id);
    }
}
