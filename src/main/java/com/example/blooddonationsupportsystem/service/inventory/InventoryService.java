package com.example.blooddonationsupportsystem.service.inventory;

import com.example.blooddonationsupportsystem.dtos.request.inventory.InventoryRequest;
import com.example.blooddonationsupportsystem.dtos.responses.ResponseObject;
import com.example.blooddonationsupportsystem.dtos.responses.inventory.InventoryResponse;
import com.example.blooddonationsupportsystem.models.BloodDonation;
import com.example.blooddonationsupportsystem.models.BloodType;
import com.example.blooddonationsupportsystem.models.Inventory;
import com.example.blooddonationsupportsystem.repositories.BloodTypeRepository;
import com.example.blooddonationsupportsystem.repositories.InventoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryService implements IInventoryService {

    private final InventoryRepository inventoryRepository;
    private final BloodTypeRepository bloodTypeRepository;

    @Override
    public Page<InventoryResponse> getAllInventory(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Inventory> inventoryPage = inventoryRepository.findAll(pageRequest);
        List<InventoryResponse> responseList = inventoryPage
                .stream()
                .map(inventory -> InventoryResponse.builder()
                        .id(inventory.getInventoryId())
                        .bloodTypeId(inventory.getBloodType().getBloodTypeId())
                        .quantity(inventory.getQuantity())
                        .lastUpdated(inventory.getLastUpdated())
                        .addedDate(inventory.getAddedDate())
                        .expiryDate(inventory.getExpiryDate())
                        .build())
                .toList();
        return new PageImpl<>(responseList, pageRequest, inventoryPage.getTotalElements());
    }

    @Override
    public InventoryResponse getInventoryById(Integer id) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cannot find inventory with id: " + id));
        return InventoryResponse.builder()
                .id(inventory.getInventoryId())
                .bloodTypeId(inventory.getBloodType().getBloodTypeId())
                .quantity(inventory.getQuantity())
                .lastUpdated(inventory.getLastUpdated())
                .addedDate(inventory.getAddedDate())
                .expiryDate(inventory.getExpiryDate())
                .build();
    }

    @Override
    public InventoryResponse createInventory(InventoryRequest request) {
        BloodType bloodType = bloodTypeRepository.findById(request.getBloodTypeId())
                .orElseThrow(() -> new RuntimeException("Cannot find blood type with id: " + request.getBloodTypeId()));

        // Check if inventory already exists for this blood type
        Optional<Inventory> optionalInventory = inventoryRepository.findByBloodType(bloodType);

        Inventory inventory;
        if (optionalInventory.isPresent()) {
            // If exists, add to quantity
            inventory = optionalInventory.get();
            inventory.setQuantity(inventory.getQuantity() + request.getQuantity());
            // Update expiry date if the new one is later
            if (request.getExpiryDate().isAfter(inventory.getExpiryDate())) {
                inventory.setExpiryDate(request.getExpiryDate());
            }
        } else {
            // If not exists, create new
            inventory = Inventory.builder()
                    .bloodType(bloodType)
                    .quantity(request.getQuantity())
                    .addedDate(request.getAddedDate())
                    .expiryDate(request.getExpiryDate())
                    .build();
        }

        inventoryRepository.save(inventory);

        return InventoryResponse.builder()
                .id(inventory.getInventoryId())
                .bloodTypeId(inventory.getBloodType().getBloodTypeId())
                .quantity(inventory.getQuantity())
                .lastUpdated(inventory.getLastUpdated())
                .addedDate(inventory.getAddedDate())
                .expiryDate(inventory.getExpiryDate())
                .build();
    }

    @Override
    public InventoryResponse updateInventory(Integer id, InventoryRequest request) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cannot find inventory with id: " + id));

        BloodType bloodType = bloodTypeRepository.findById(request.getBloodTypeId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Cannot find blood type with id: " + request.getBloodTypeId()));

        inventory.setBloodType(bloodType);
        inventory.setQuantity(request.getQuantity());
        inventory.setAddedDate(request.getAddedDate());
        inventory.setExpiryDate(request.getExpiryDate());
        inventory.setLastUpdated(LocalDateTime.now());
        
        inventoryRepository.save(inventory);
        
        return InventoryResponse.builder()
                .id(inventory.getInventoryId())
                .bloodTypeId(inventory.getBloodType().getBloodTypeId())
                .quantity(inventory.getQuantity())
                .lastUpdated(inventory.getLastUpdated())
                .addedDate(inventory.getAddedDate())
                .expiryDate(inventory.getExpiryDate())
                .build();
    }
    
    @Override
    public ResponseEntity<ResponseObject> deleteInventory(Integer id) {
        try {
            Inventory inventory = inventoryRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Cannot find inventory with id: " + id));
            
            inventoryRepository.delete(inventory);
            
            return ResponseEntity.ok(
                    ResponseObject.builder()
                            .status(HttpStatus.OK)
                            .message("Successfully deleted inventory")
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseObject.builder()
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .message("Error deleting inventory: " + e.getMessage())
                            .build());
        }
    }
    
    @Override
    public List<InventoryResponse> findByBloodTypeAndNotExpired(Integer bloodTypeId, LocalDate date) {
        BloodType bloodType = bloodTypeRepository.findById(bloodTypeId)
                .orElseThrow(() -> new RuntimeException("Cannot find blood type with id: " + bloodTypeId));
        
        List<Inventory> inventories = inventoryRepository.findByBloodTypeAndExpiryDateGreaterThanEqual(bloodType, date);
        
        return inventories.stream()
                .map(inventory -> InventoryResponse.builder()
                        .id(inventory.getInventoryId())
                        .bloodTypeId(inventory.getBloodType().getBloodTypeId())
                        .quantity(inventory.getQuantity())
                        .lastUpdated(inventory.getLastUpdated())
                        .addedDate(inventory.getAddedDate())
                        .expiryDate(inventory.getExpiryDate())
                        .build())
                .collect(Collectors.toList());
    }
    
    @Override
    public List<InventoryResponse> findExpiredInventory(LocalDate date) {
        List<Inventory> inventories = inventoryRepository.findByExpiryDateBefore(date);
        
        return inventories.stream()
                .map(inventory -> InventoryResponse.builder()
                        .id(inventory.getInventoryId())
                        .bloodTypeId(inventory.getBloodType().getBloodTypeId())
                        .quantity(inventory.getQuantity())
                        .lastUpdated(inventory.getLastUpdated())
                        .addedDate(inventory.getAddedDate())
                        .expiryDate(inventory.getExpiryDate())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void updateInventoryAfterDonation(BloodDonation bloodDonation) {
        BloodType bloodType = bloodDonation.getBloodType();
        Integer volume = bloodDonation.getVolumeMl();
        
        // Calculate expiry date (e.g., 35 days from now)
        LocalDate expiryDate = LocalDate.now().plusDays(35);

        Inventory inventory = inventoryRepository.findByBloodType(bloodType)
                .orElseGet(() -> {
                    Inventory newInventory = Inventory.builder()
                            .bloodType(bloodType)
                            .quantity(0)
                            .addedDate(LocalDate.now())
                            .expiryDate(expiryDate)
                            .lastUpdated(LocalDateTime.now())
                            .build();
                    return inventoryRepository.save(newInventory);
                });
        inventory.setQuantity(inventory.getQuantity() + volume);
        inventory.setLastUpdated(LocalDateTime.now());
        
        // Update expiry date if needed
        if (expiryDate.isAfter(inventory.getExpiryDate())) {
            inventory.setExpiryDate(expiryDate);
        }

        inventoryRepository.save(inventory);
    }
}
