package com.example.blooddonationsupportsystem.controller;

import com.example.blooddonationsupportsystem.dtos.request.inventory.InventoryRequest;
import com.example.blooddonationsupportsystem.dtos.responses.ResponseObject;
import com.example.blooddonationsupportsystem.dtos.responses.inventory.InventoryResponse;
import com.example.blooddonationsupportsystem.service.inventory.IInventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

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
        inventoryService.createInventory(inventoryRequest);
        return ResponseEntity.ok(
                ResponseObject.builder()
                        .status(HttpStatus.OK)
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
        inventoryService.updateInventory(id, inventoryRequest);
        return ResponseEntity.ok(
                ResponseObject.builder()
                        .status(HttpStatus.OK)
                        .message("Successfully updated inventory")
                        .build()
        );
    }

    /**
     * Retrieves all inventory records.
     *
     * @return a {@link ResponseEntity} containing the response object with a success
     *         message and a list of all inventory records
     */
    @GetMapping
    public ResponseEntity<?> getAllInventory() {
        return ResponseEntity.ok(
                ResponseObject.builder()
                        .status(HttpStatus.OK)
                        .message("Successfully get all inventory")
                        .data(inventoryService.getAllInventory())
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
}
