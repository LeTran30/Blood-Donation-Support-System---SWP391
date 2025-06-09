package com.example.blooddonationsupportsystem.controller;

import com.example.blooddonationsupportsystem.dtos.responses.ResponseObject;
import com.example.blooddonationsupportsystem.dtos.responses.bloodType.BloodTypeResponse;
import com.example.blooddonationsupportsystem.service.bloodType.IBloodTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/blood-type")
@RequiredArgsConstructor
@CrossOrigin("*")
public class BloodTypeController {
    private final IBloodTypeService bloodTypeService;

    /**
     * Retrieves the list of all available blood types from the system.
     *
     * @return ResponseEntity containing a ResponseObject with a message,
     *         HTTP status, and a list of BloodTypeResponse objects representing
     *         the available blood types.
     */
    @GetMapping
    @PreAuthorize("permitAll()")
    ResponseEntity<ResponseObject> getBloodType(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<BloodTypeResponse> bloodTypes = bloodTypeService.getAllBloodTypes(page, size);
        return ResponseEntity.ok(ResponseObject.builder()
                .message("Get list of blood Components successfully")
                .status(HttpStatus.OK)
                .data(bloodTypes)
                .build());
    }
}
