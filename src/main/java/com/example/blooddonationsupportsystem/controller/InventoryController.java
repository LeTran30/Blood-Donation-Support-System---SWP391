package com.example.blooddonationsupportsystem.controller;

import com.example.blooddonationsupportsystem.dtos.request.inventory.InventoryRequest;
import com.example.blooddonationsupportsystem.dtos.responses.ResponseObject;
import com.example.blooddonationsupportsystem.dtos.responses.inventory.InventoryResponse;
import com.example.blooddonationsupportsystem.service.inventory.IInventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
@CrossOrigin("*")
public class InventoryController {
    private final IInventoryService inventoryService;

    @PostMapping
//    @PreAuthorize("permitAll()")
    public ResponseEntity<?> addInventory(
            @Valid @RequestBody InventoryRequest inventoryRequest,
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
                            .message(String.join(", ", errorMessages))
                            .build()
            );
        }
        InventoryResponse response = inventoryService.createInventory(inventoryRequest);
        return ResponseEntity.ok(
                ResponseObject.builder()
                        .status(HttpStatus.OK)
                        .data(response)
                        .message("Successfully created inventory")
                        .build()
        );
    }

    @PutMapping("/{id}")
//    @PreAuthorize("permitAll()")
    public ResponseEntity<?> updateInventory(
            @PathVariable Integer id,
            @Valid @RequestBody InventoryRequest inventoryRequest,
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
                            .message(String.join(", ", errorMessages))
                            .build()
            );
        }
        InventoryResponse response = inventoryService.updateInventory(id, inventoryRequest);
        return ResponseEntity.ok(
                ResponseObject.builder()
                        .status(HttpStatus.OK)
                        .data(response)
                        .message("Successfully updated inventory")
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteInventory(@PathVariable Integer id) {
        return inventoryService.deleteInventory(id);
    }

    /**
     * Retrieves all inventory records.
     *
     * @return a {@link ResponseEntity} containing the response object with a success
     *         message and a list of all inventory records
     */
    @GetMapping
    public ResponseEntity<?> getAllInventory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<InventoryResponse> response = inventoryService.getAllInventory(page, size);
        return ResponseEntity.ok(
                ResponseObject.builder()
                        .status(HttpStatus.OK)
                        .message("Successfully get all inventory")
                        .data(response)
                        .build()
        );
    }

    /**
     * Retrieves inventory details by the specified ID.
     *
     * @param id the ID of the inventory to be retrieved
     * @return a {@link ResponseEntity} containing the response object with the
     *         inventory details and a success message
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getInventoryById(
            @PathVariable("id") Integer id
    ) {
        InventoryResponse inventoryResponse = inventoryService.getInventoryById(id);
        return ResponseEntity.ok(
                ResponseObject.builder()
                        .status(HttpStatus.OK)
                        .message("Successfully get inventory by id")
                        .data(inventoryResponse)
                        .build()
        );
    }
    
    /**
     * Retrieves non-expired inventory items for a specific blood type.
     *
     * @param bloodTypeId the ID of the blood type
     * @param date the date to check against expiry date (defaults to current date)
     * @return a {@link ResponseEntity} containing the response object with the
     *         inventory items and a success message
     */
    @GetMapping("/blood-type/{bloodTypeId}/not-expired")
    public ResponseEntity<?> getInventoryByBloodTypeAndNotExpired(
            @PathVariable Integer bloodTypeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        if (date == null) {
            date = LocalDate.now();
        }
        
        List<InventoryResponse> inventories = inventoryService.findByBloodTypeAndNotExpired(bloodTypeId, date);
        
        return ResponseEntity.ok(
                ResponseObject.builder()
                        .status(HttpStatus.OK)
                        .message("Successfully retrieved non-expired inventory for blood type")
                        .data(inventories)
                        .build()
        );
    }
    
    /**
     * Retrieves expired inventory items.
     *
     * @param date the date to check against expiry date (defaults to current date)
     * @return a {@link ResponseEntity} containing the response object with the
     *         expired inventory items and a success message
     */
    @GetMapping("/expired")
    public ResponseEntity<?> getExpiredInventory(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        if (date == null) {
            date = LocalDate.now();
        }
        
        List<InventoryResponse> inventories = inventoryService.findExpiredInventory(date);
        
        return ResponseEntity.ok(
                ResponseObject.builder()
                        .status(HttpStatus.OK)
                        .message("Successfully retrieved expired inventory")
                        .data(inventories)
                        .build()
        );
    }
}
