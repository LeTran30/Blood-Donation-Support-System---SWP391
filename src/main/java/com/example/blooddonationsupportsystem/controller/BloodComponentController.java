package com.example.blooddonationsupportsystem.controller;

import com.example.blooddonationsupportsystem.dtos.responses.bloodComponent.BloodComponentResponse;
import com.example.blooddonationsupportsystem.dtos.responses.ResponseObject;
import com.example.blooddonationsupportsystem.service.bloodComponent.IBloodComponentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
     *         and the list of BloodComponentResponse objects representing blood components.
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
}
