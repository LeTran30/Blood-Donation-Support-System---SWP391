package com.example.blooddonationsupportsystem.service.bloodRequest;

import com.example.blooddonationsupportsystem.dtos.request.bloodRequest.BloodRequestAllocationRequest;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;

public interface IBloodRequestService {
    ResponseEntity<?> createRequest(BloodRequestAllocationRequest request);

    ResponseEntity<?> getAllRequests(int page, int size);

    ResponseEntity<?> getInventoryForRequest(Integer requestId, int page, int size);

    @Transactional
    ResponseEntity<?> allocateInventory(Integer requestId);
}
