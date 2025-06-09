package com.example.blooddonationsupportsystem.service.bloodRequest;

import com.example.blooddonationsupportsystem.dtos.request.bloodrequest.BloodRequestAllocationRequest;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;

public interface IBloodRequestService {
    ResponseEntity<?> createRequest(BloodRequestAllocationRequest request);

    ResponseEntity<?> getAllRequests(int page, int size);

    ResponseEntity<?> getInventoryForRequest(Integer requestId, int page, int size);

    @Transactional
    ResponseEntity<?> allocateInventory(Integer requestId);
}
