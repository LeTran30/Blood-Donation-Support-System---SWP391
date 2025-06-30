package com.example.blooddonationsupportsystem.service.bloodRequest;

import com.example.blooddonationsupportsystem.dtos.request.bloodRequest.BloodRequestAllocationRequest;
import com.example.blooddonationsupportsystem.dtos.responses.ResponseObject;
import com.example.blooddonationsupportsystem.dtos.responses.bloodRequest.BloodRequestResponse;
import com.example.blooddonationsupportsystem.dtos.responses.inventoryAllocation.InventoryAllocationResponse;
import com.example.blooddonationsupportsystem.models.*;
import com.example.blooddonationsupportsystem.repositories.*;
import com.example.blooddonationsupportsystem.utils.BloodRequestInventoryKey;
import com.example.blooddonationsupportsystem.utils.RequestStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BloodRequestService implements IBloodRequestService {
    private final BloodRequestRepository bloodRequestRepository;
    private final BloodRequestInventoryRepository bloodRequestInventoryRepository;
    private final InventoryRepository inventoryRepository;
    private final BloodComponentRepository bloodComponentRepository;
    private final BloodTypeRepository bloodTypeRepository;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;

    @Override
    public ResponseEntity<?> createRequest(BloodRequestAllocationRequest request) {
        BloodRequest bloodRequest = modelMapper.map(request, BloodRequest.class);

        if (bloodRequest.getCreatedAt() == null) {
            bloodRequest.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        }

        BloodType bloodType = bloodTypeRepository.findById(request.getBloodTypeId())
                .orElseThrow(() -> new RuntimeException("Blood type not found"));
        BloodComponent bloodComponent = bloodComponentRepository.findById(request.getBloodComponentId())
                .orElseThrow(() -> new RuntimeException("Blood component not found"));

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        bloodRequest.setBloodType(bloodType);
        bloodRequest.setComponent(bloodComponent);
        bloodRequest.setStatus(RequestStatus.PENDING);
        bloodRequest.setCreatedBy(user);
        bloodRequest = bloodRequestRepository.save(bloodRequest);

        BloodRequestResponse response = toResponseDTO(bloodRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseObject.builder()
                        .status(HttpStatus.CREATED)
                        .message("Blood request created successfully")
                        .data(response)
                        .build());
    }

    @Override
    public ResponseEntity<?> getAllRequests(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<BloodRequest> requests = bloodRequestRepository.findAll(pageable);

        List<BloodRequestResponse> responses = requests.getContent().stream()
                .map(this::toResponseDTO)
                .toList();

        return ResponseEntity.ok(
                ResponseObject.builder()
                        .status(HttpStatus.OK)
                        .message("List of blood requests retrieved successfully")
                        .data(Map.of(
                                "content", responses,
                                "page", Map.of(
                                        "size", requests.getSize(),
                                        "number", requests.getNumber(),
                                        "totalElements", requests.getTotalElements(),
                                        "totalPages", requests.getTotalPages()
                                )
                        ))
                        .build()
        );
    }

    @Override
    public ResponseEntity<?> getInventoryForRequest(Integer requestId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<BloodRequestInventory> inventoryAllocations =
                bloodRequestInventoryRepository.findByBloodRequestRequestId(requestId, pageable);

        if (inventoryAllocations.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseObject.builder()
                            .status(HttpStatus.NOT_FOUND)
                            .message("No inventory allocations found for this blood request")
                            .build());
        }

        List<InventoryAllocationResponse> responseList = inventoryAllocations.getContent().stream()
                .map(this::mapToInventoryAllocationResponse)
                .toList();

        return ResponseEntity.ok(
                ResponseObject.builder()
                        .status(HttpStatus.OK)
                        .message("Inventory allocations retrieved successfully")
                        .data(Map.of(
                                "content", responseList,
                                "page", Map.of(
                                        "size", inventoryAllocations.getSize(),
                                        "number", inventoryAllocations.getNumber(),
                                        "totalElements", inventoryAllocations.getTotalElements(),
                                        "totalPages", inventoryAllocations.getTotalPages()
                                )
                        ))
                        .build()
        );
    }

    @Transactional
    @Override
    public ResponseEntity<?> allocateInventory(Integer requestId) {
        Optional<BloodRequest> optionalRequest = bloodRequestRepository.findById(Long.valueOf(requestId));
        if (optionalRequest.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseObject.builder()
                            .status(HttpStatus.NOT_FOUND)
                            .message("Blood request not found")
                            .build());
        }

        BloodRequest bloodRequest = optionalRequest.get();
        int requestedQuantity = bloodRequest.getQuantity();

        Optional<Inventory> optionalInventory = inventoryRepository.findByBloodTypeAndBloodComponent(
                bloodRequest.getBloodType(), bloodRequest.getComponent());

        if (optionalInventory.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseObject.builder()
                            .status(HttpStatus.NOT_FOUND)
                            .message("No inventory available for this blood request")
                            .build());
        }

        Inventory inventory = optionalInventory.get();

        if (inventory.getQuantity() < requestedQuantity) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseObject.builder()
                            .status(HttpStatus.BAD_REQUEST)
                            .message("Not enough inventory to fulfill the request")
                            .build());
        }

        inventory.setQuantity(inventory.getQuantity() - requestedQuantity);
        inventoryRepository.save(inventory);

        Optional<BloodRequestInventory> optionalBRInventory = bloodRequestInventoryRepository
                .findByBloodRequestAndInventory(bloodRequest, inventory);

        BloodRequestInventory brInventory = optionalBRInventory.orElseGet(() -> {
            BloodRequestInventory newBRInventory = new BloodRequestInventory();
            BloodRequestInventoryKey key = new BloodRequestInventoryKey(
                    bloodRequest.getRequestId(), inventory.getInventoryId()
            );
            newBRInventory.setId(key);
            newBRInventory.setBloodRequest(bloodRequest);
            newBRInventory.setInventory(inventory);
            newBRInventory.setAllocatedQuantity(0);
            return newBRInventory;
        });

        brInventory.setAllocatedQuantity(brInventory.getAllocatedQuantity() + requestedQuantity);
        bloodRequestInventoryRepository.save(brInventory);

        bloodRequest.setStatus(RequestStatus.ALLOCATED);
        bloodRequestRepository.save(bloodRequest);

        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.OK)
                .message("Allocated " + requestedQuantity + " units of inventory successfully")
                .build());
    }

    private InventoryAllocationResponse mapToInventoryAllocationResponse(BloodRequestInventory allocation) {
        Inventory inventory = allocation.getInventory();
        return InventoryAllocationResponse.builder()
                .requestId(allocation.getId().getRequestId())
                .inventoryId(allocation.getId().getInventoryId())
                .bloodTypeId(
                        inventory.getBloodType() != null ? inventory.getBloodType().getBloodTypeId() : null
                )
                .bloodComponentId(
                        inventory.getBloodComponent() != null ? inventory.getBloodComponent().getComponentId() : null
                )
                .allocatedQuantity(allocation.getAllocatedQuantity())
                .build();
    }


    private BloodRequestResponse toResponseDTO(BloodRequest bloodRequest) {
        return BloodRequestResponse.builder()
                .requestId(bloodRequest.getRequestId())
                .componentId(bloodRequest.getComponent().getComponentId())
                .bloodTypeId(bloodRequest.getBloodType().getBloodTypeId())
                .urgencyLevel(bloodRequest.getUrgencyLevel())
                .status(bloodRequest.getStatus().toString())
                .quantity(bloodRequest.getQuantity())
                .createdAt(bloodRequest.getCreatedAt() != null
                        ? bloodRequest.getCreatedAt().toLocalDateTime().toString()
                        : null)
                .createdBy(bloodRequest.getCreatedBy().getId())
                .build();
    }

}
