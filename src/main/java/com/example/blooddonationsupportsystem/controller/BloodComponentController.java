package com.example.blooddonationsupportsystem.controller;

import com.example.blooddonationsupportsystem.dtos.responses.bloodComponent.BloodComponentResponse;
import com.example.blooddonationsupportsystem.dtos.responses.ResponseObject;
import com.example.blooddonationsupportsystem.service.bloodComponent.IBloodComponentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    ResponseEntity<ResponseObject> getBloodComponent() {
        List<BloodComponentResponse> bloodComponents = bloodComponentService.getAllBloodComponents();
        return ResponseEntity.ok(ResponseObject.builder()
                .message("Get list of blood Components successfully")
                .status(HttpStatus.OK)
                .data(bloodComponents)
                .build());
    }
}
