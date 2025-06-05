package com.example.blooddonationsupportsystem.service.bloodRequest;

import com.example.blooddonationsupportsystem.dtos.request.bloodrequest.BloodRequestRequest;
import org.springframework.http.ResponseEntity;

public interface IBloodRequestService {
    ResponseEntity<?> createRequest(BloodRequestRequest request);

    ResponseEntity<?> getAllRequests();

    ResponseEntity<?> getInventoryForRequest(Integer requestId);
}
