package com.example.blooddonationsupportsystem.service.bloodRequestInventory;

import com.example.blooddonationsupportsystem.dtos.request.bloodRequestInventory.BloodRequestInventoryRequest;
import com.example.blooddonationsupportsystem.dtos.responses.ResponseObject;
import com.example.blooddonationsupportsystem.models.BloodRequest;
import com.example.blooddonationsupportsystem.models.BloodRequestInventory;
import com.example.blooddonationsupportsystem.models.Inventory;
import com.example.blooddonationsupportsystem.repositories.BloodRequestInventoryRepository;
import com.example.blooddonationsupportsystem.repositories.BloodRequestRepository;
import com.example.blooddonationsupportsystem.repositories.InventoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BloodRequestInventoryService implements IBloodRequestInventoryService {
    private final BloodRequestRepository bloodRequestRepository;
    private final InventoryRepository inventoryRepository;
    private final BloodRequestInventoryRepository bloodRequestInventoryRepository;

    @Transactional
    @Override
    public ResponseEntity<?> assignInventory(BloodRequestInventoryRequest request) {
        BloodRequest bloodRequest = bloodRequestRepository.findById(Long.valueOf(request.getBloodRequestId()))
                .orElse(null);
        if (bloodRequest == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseObject.builder()
                            .status(HttpStatus.NOT_FOUND)
                            .message("Blood request not found")
                            .build());
        }

        Inventory bloodInventory = inventoryRepository.findById(request.getInventoryId())
                .orElse(null);
        if (bloodInventory == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseObject.builder()
                            .status(HttpStatus.NOT_FOUND)
                            .message("Blood inventory not found")
                            .build());
        }

        if (bloodInventory.getQuantity() < request.getQuantity()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseObject.builder()
                            .status(HttpStatus.BAD_REQUEST)
                            .message("Not enough inventory available")
                            .build());
        }

        // Deduct from inventory
        bloodInventory.setQuantity(bloodInventory.getQuantity() - request.getQuantity());
        inventoryRepository.save(bloodInventory);

        // Save allocation
        BloodRequestInventory allocation = BloodRequestInventory.builder()
                .bloodRequest(bloodRequest)
                .inventory(bloodInventory)
                .allocatedQuantity(request.getQuantity())
                .build();
        bloodRequestInventoryRepository.save(allocation);

        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.OK)
                .message("Inventory assigned successfully")
                .data(allocation)
                .build());
    }
}
