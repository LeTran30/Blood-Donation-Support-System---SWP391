package com.example.blooddonationsupportsystem.service.bloodRequest;

import com.example.blooddonationsupportsystem.dtos.request.bloodrequest.BloodRequestRequest;
import com.example.blooddonationsupportsystem.dtos.responses.ResponseObject;
import com.example.blooddonationsupportsystem.dtos.responses.bloodRequest.BloodRequestResponse;
import com.example.blooddonationsupportsystem.dtos.responses.inventoryAllocation.InventoryAllocationResponse;
import com.example.blooddonationsupportsystem.models.BloodRequest;
import com.example.blooddonationsupportsystem.models.BloodRequestInventory;
import com.example.blooddonationsupportsystem.repositories.BloodRequestInventoryRepository;
import com.example.blooddonationsupportsystem.repositories.BloodRequestRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BloodRequestService implements IBloodRequestService {
    private final BloodRequestRepository bloodRequestRepository;
    private final BloodRequestInventoryRepository bloodRequestInventoryRepository;
    private final ModelMapper modelMapper;

    @Override
    public ResponseEntity<?> createRequest(BloodRequestRequest request) {
        BloodRequest bloodRequest = modelMapper.map(request, BloodRequest.class);

        if (bloodRequest.getCreatedAt() == null) {
            bloodRequest.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        }

        bloodRequest = bloodRequestRepository.save(bloodRequest);

        BloodRequestResponse response = modelMapper.map(bloodRequest, BloodRequestResponse.class);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseObject.builder()
                        .status(HttpStatus.CREATED)
                        .message("Blood request created successfully")
                        .data(response)
                        .build());
    }

    @Override
    public ResponseEntity<?> getAllRequests() {
        List<BloodRequest> requests = bloodRequestRepository.findAll();
        List<BloodRequestResponse> responses = requests.stream()
                .map(req -> modelMapper.map(req, BloodRequestResponse.class))
                .toList();

        return ResponseEntity.ok(
                ResponseObject.builder()
                        .status(HttpStatus.OK)
                        .message("List of blood requests retrieved successfully")
                        .data(responses)
                        .build()
        );
    }

    public ResponseEntity<?> getInventoryForRequest(Integer requestId) {
        List<BloodRequestInventory> inventoryAllocations = bloodRequestInventoryRepository.findByBloodRequestRequestId(requestId);

        if (inventoryAllocations.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseObject.builder()
                            .status(HttpStatus.NOT_FOUND)
                            .message("No inventory allocations found for this blood request")
                            .build());
        }

        List<InventoryAllocationResponse> responseList = inventoryAllocations.stream()
                .map(this::mapToInventoryAllocationResponse)
                .toList();

        return ResponseEntity.ok(
                ResponseObject.builder()
                        .status(HttpStatus.OK)
                        .message("Inventory allocations retrieved successfully")
                        .data(responseList)
                        .build()
        );

    }
    private InventoryAllocationResponse mapToInventoryAllocationResponse(BloodRequestInventory allocation) {
        return InventoryAllocationResponse.builder()
                .allocationId(allocation.getId())
                .inventoryId(allocation.getInventory().getInventoryId())
                .bloodType(allocation.getInventory().getBloodType().getBloodTypeId())
                .bloodComponent(allocation.getInventory().getBloodComponent().getComponentId())
                .quantityAllocated(allocation.getQuantityAllocated())
                .build();
    }
}
