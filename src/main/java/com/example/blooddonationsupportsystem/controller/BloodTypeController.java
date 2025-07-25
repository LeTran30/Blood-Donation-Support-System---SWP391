package com.example.blooddonationsupportsystem.controller;

import com.example.blooddonationsupportsystem.dtos.request.bloodType.BloodTypeRequest;
import com.example.blooddonationsupportsystem.dtos.responses.ResponseObject;
import com.example.blooddonationsupportsystem.dtos.responses.bloodType.BloodTypeResponse;
import com.example.blooddonationsupportsystem.exceptions.EntityNotFoundException;
import com.example.blooddonationsupportsystem.service.bloodType.IBloodTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

    @PostMapping
    public ResponseEntity<BloodTypeResponse> create(@RequestBody BloodTypeRequest request) {
        return ResponseEntity.ok(bloodTypeService.createBloodType(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BloodTypeResponse> update(@PathVariable Integer id, @RequestBody BloodTypeRequest request) {
        return ResponseEntity.ok(bloodTypeService.updateBloodType(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        bloodTypeService.deleteBloodType(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/components")
    public ResponseEntity<BloodTypeResponse> assignComponents(
            @PathVariable Integer id,
            @RequestBody Map<String, List<Integer>> body
    ) {
        List<Integer> componentIds = body.get("componentIds");
        BloodTypeResponse response = bloodTypeService.assignComponentsToBloodType(id, componentIds);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{bloodTypeId}/components/{componentId}")
    public ResponseEntity<BloodTypeResponse> removeComponent(
            @PathVariable Integer bloodTypeId,
            @PathVariable Integer componentId
    ) {
        BloodTypeResponse response = bloodTypeService.removeComponentFromBloodType(bloodTypeId, componentId);
        return ResponseEntity.ok(response);
    }
}
