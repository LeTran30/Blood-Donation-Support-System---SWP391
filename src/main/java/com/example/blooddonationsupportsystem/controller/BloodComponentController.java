package com.example.blooddonationsupportsystem.controller;

import com.example.blooddonationsupportsystem.dtos.request.bloodComponent.BloodComponentRequest;
import com.example.blooddonationsupportsystem.dtos.responses.bloodComponent.BloodComponentResponse;
import com.example.blooddonationsupportsystem.dtos.responses.ResponseObject;
import com.example.blooddonationsupportsystem.service.bloodComponent.IBloodComponentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/blood-component")
@CrossOrigin("*")
@RequiredArgsConstructor
public class BloodComponentController {
    private final IBloodComponentService bloodComponentService;

    /**
     * Retrieves a list of all blood components.
     *
     * @return ResponseEntity containing a ResponseObject with a message, HTTP status,
     * and the list of BloodComponentResponse objects representing blood components.
     */
    @GetMapping
//    @PreAuthorize("permitAll()")
    ResponseEntity<ResponseObject> getBloodComponent(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<BloodComponentResponse> bloodComponents = bloodComponentService.getAllBloodComponents(page, size);
        return ResponseEntity.ok(ResponseObject.builder()
                .message("Get list of blood Components successfully")
                .status(HttpStatus.OK)
                .data(bloodComponents)
                .build());
    }

    @PostMapping
    public ResponseEntity<?> create(
            @Valid @RequestBody BloodComponentRequest request,
            BindingResult result
    ) {
        if (result.hasErrors()) {
            List<String> errors = result.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(
                    ResponseObject.builder()
                            .status(HttpStatus.BAD_REQUEST)
                            .message(String.join(", ", errors))
                            .build()
            );
        }

        bloodComponentService.createBloodComponent(request);
        return ResponseEntity.ok(
                ResponseObject.builder()
                        .status(HttpStatus.OK)
                        .message("Created blood component successfully")
                        //.data(response)
                        .build()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Integer id,
            @Valid @RequestBody BloodComponentRequest request,
            BindingResult result
    ) {
        if (result.hasErrors()) {
            List<String> errors = result.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(
                    ResponseObject.builder()
                            .status(HttpStatus.BAD_REQUEST)
                            .message(String.join(", ", errors))
                            .build()
            );
        }

        bloodComponentService.updateBloodComponent(id, request);
        return ResponseEntity.ok(
                ResponseObject.builder()
                        .status(HttpStatus.OK)
                        .message("Updated blood component successfully")
//                        .data(response)
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        bloodComponentService.deleteBloodComponent(id);
        return ResponseEntity.ok(
                ResponseObject.builder()
                        .status(HttpStatus.OK)
                        .message("Deleted blood component successfully")
                        .build()
        );
    }
}
