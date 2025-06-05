package com.example.blooddonationsupportsystem.service.bloodRequestInventory;

import com.example.blooddonationsupportsystem.dtos.request.bloodRequestInventory.BloodRequestInventoryRequest;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;

public interface IBloodRequestInventoryService {
    @Transactional
    ResponseEntity<?> assignInventory(BloodRequestInventoryRequest request);
}
