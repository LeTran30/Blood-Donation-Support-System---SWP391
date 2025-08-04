package com.example.blooddonationsupportsystem.service.inventory;

import com.example.blooddonationsupportsystem.dtos.request.inventory.InventoryRequest;
import com.example.blooddonationsupportsystem.dtos.request.inventory.InventoryUpdateRequest;
import com.example.blooddonationsupportsystem.dtos.responses.ResponseObject;
import com.example.blooddonationsupportsystem.dtos.responses.inventory.InventoryResponse;
import com.example.blooddonationsupportsystem.models.BloodComponent;
import com.example.blooddonationsupportsystem.models.BloodDonation;
import com.example.blooddonationsupportsystem.models.BloodType;
import com.example.blooddonationsupportsystem.models.Inventory;
import com.example.blooddonationsupportsystem.repositories.BloodComponentRepository;
import com.example.blooddonationsupportsystem.repositories.BloodTypeRepository;
import com.example.blooddonationsupportsystem.repositories.InventoryRepository;
import com.example.blooddonationsupportsystem.utils.InventoryStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
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
    private final BloodComponentRepository bloodComponentRepository;

    @Override
    public Page<InventoryResponse> getAllInventory(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Inventory> inventoryPage = inventoryRepository.findAll(pageRequest);
        List<InventoryResponse> responseList = inventoryPage
                .stream()
                .map(inventory -> InventoryResponse.builder()
                        .id(inventory.getInventoryId())
                        .componentId(inventory.getBloodComponent().getComponentId())
                        .bloodTypeId(inventory.getBloodType().getBloodTypeId())
                        .quantity(inventory.getQuantity())
                        .lastUpdated(inventory.getLastUpdated())
                        .addedDate(inventory.getAddedDate())
                        .expiryDate(inventory.getExpiryDate())
                        .batchNumber(inventory.getBatchNumber())
                        .status(inventory.getStatus())
                        .build())
                .toList();
        return new PageImpl<>(responseList, pageRequest, inventoryPage.getTotalElements());
    }

    @Override
    public InventoryResponse getInventoryById(Integer id) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục có ID: " + id));
        return InventoryResponse.builder()
                .id(inventory.getInventoryId())
                .bloodTypeId(inventory.getBloodType().getBloodTypeId())
                .componentId(inventory.getBloodComponent().getComponentId())
                .quantity(inventory.getQuantity())
                .lastUpdated(inventory.getLastUpdated())
                .addedDate(inventory.getAddedDate())
                .expiryDate(inventory.getExpiryDate())
                .batchNumber(inventory.getBatchNumber())
                .status(inventory.getStatus())
                .build();
    }

    @Override
    public InventoryResponse createInventory(InventoryRequest request) {
        BloodType bloodType = bloodTypeRepository.findById(request.getBloodTypeId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhóm máu có ID: " + request.getBloodTypeId()));

        BloodComponent component = bloodComponentRepository.findById(request.getComponentId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thành phần máu có ID: " + request.getComponentId()));
        Optional<Inventory> optionalInventory = inventoryRepository
                .findByBloodTypeAndBloodComponentAndBatchNumber(bloodType, component, request.getBatchNumber());
        // Lookup by bloodType + component + batchNumber (UNIQUE)
        if (optionalInventory.isPresent()) {
            throw new RuntimeException("Đã tồn tại kho máu có cùng số lô. Không thể tạo bản sao.");

        }

        Inventory inventory = Inventory.builder()
                .bloodType(bloodType)
                .bloodComponent(component)
                .quantity(request.getQuantity())
                .batchNumber(request.getBatchNumber())
                .addedDate(request.getAddedDate())
                .expiryDate(request.getExpiryDate())
                .status(InventoryStatus.AVAILABLE)
                .build();

        inventoryRepository.save(inventory);

        return InventoryResponse.builder()
                .id(inventory.getInventoryId())
                .bloodTypeId(inventory.getBloodType().getBloodTypeId())
                .componentId(inventory.getBloodComponent().getComponentId())
                .quantity(inventory.getQuantity())
                .lastUpdated(inventory.getLastUpdated())
                .addedDate(inventory.getAddedDate())
                .expiryDate(inventory.getExpiryDate())
                .batchNumber(inventory.getBatchNumber())
                .status(inventory.getStatus())
                .build();
    }


    @Override
    public InventoryResponse updateInventory(Integer id, InventoryUpdateRequest request) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục có ID: " + id));

        BloodType bloodType = bloodTypeRepository.findById(request.getBloodTypeId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Không tìm thấy nhóm máu có ID: " + request.getBloodTypeId()));
        BloodComponent component = bloodComponentRepository.findById(request.getComponentId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Không tìm thấy thành phần máu có ID: " + request.getComponentId()
                        ));
        inventory.setBloodType(bloodType);
        inventory.setBloodComponent(component);
        inventory.setQuantity(request.getQuantity());
        inventory.setAddedDate(request.getAddedDate());
        inventory.setExpiryDate(request.getExpiryDate());
        inventory.setLastUpdated(LocalDateTime.now());
        if (inventory.getStatus() == InventoryStatus.USED && request.getQuantity() > 0) {
            inventory.setStatus(InventoryStatus.AVAILABLE);
        }
        inventoryRepository.save(inventory);
        
        return InventoryResponse.builder()
                .id(inventory.getInventoryId())
                .bloodTypeId(inventory.getBloodType().getBloodTypeId())
                .componentId(inventory.getBloodComponent().getComponentId())
                .quantity(inventory.getQuantity())
                .lastUpdated(inventory.getLastUpdated())
                .addedDate(inventory.getAddedDate())
                .expiryDate(inventory.getExpiryDate())
                .batchNumber(inventory.getBatchNumber())
                .status(inventory.getStatus())
                .build();
    }
    
    @Override
    public ResponseEntity<ResponseObject> deleteInventory(Integer id) {
        try {
            Inventory inventory = inventoryRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục có ID: " + id));
            
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
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhóm máu có ID: " + bloodTypeId));

        List<Inventory> inventories = inventoryRepository.findByBloodTypeAndExpiryDateGreaterThanEqual(bloodType, date);
        
        return inventories.stream()
                .map(inventory -> InventoryResponse.builder()
                        .id(inventory.getInventoryId())
                        .bloodTypeId(inventory.getBloodType().getBloodTypeId())
                        .componentId(inventory.getBloodComponent().getComponentId())
                        .quantity(inventory.getQuantity())
                        .lastUpdated(inventory.getLastUpdated())
                        .addedDate(inventory.getAddedDate())
                        .expiryDate(inventory.getExpiryDate())
                        .batchNumber(inventory.getBatchNumber())
                        .status(inventory.getStatus())
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
                        .componentId(inventory.getBloodComponent().getComponentId())
                        .quantity(inventory.getQuantity())
                        .lastUpdated(inventory.getLastUpdated())
                        .addedDate(inventory.getAddedDate())
                        .batchNumber(inventory.getBatchNumber())
                        .status(inventory.getStatus())
                        .expiryDate(inventory.getExpiryDate())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void updateInventoryAfterDonation(BloodDonation bloodDonation) {
        BloodType bloodType = bloodDonation.getBloodType();
        BloodComponent bloodComponent = bloodDonation.getBloodComponent();
        Integer volume = bloodDonation.getVolumeMl();
        
        // Calculate expiry date (e.g., 35 days from now)
        LocalDate expiryDate = LocalDate.now().plusDays(35);

        Inventory inventory = inventoryRepository.findByBloodType(bloodType)
                .orElseGet(() -> {
                    Inventory newInventory = Inventory.builder()
                            .bloodType(bloodType)
                            .bloodComponent(bloodComponent)
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

    private InventoryStatus determineStatus(Integer quantity, LocalDate expiryDate) {
        if (quantity == 0) return InventoryStatus.USED;
        if (expiryDate.isBefore(LocalDate.now())) return InventoryStatus.EXPIRED;
        return InventoryStatus.AVAILABLE;
    }

    @Scheduled(cron = "0 0 0 * * ?") // Chạy mỗi ngày lúc 00:00
    @Transactional
    public void updateExpiredInventories() {
        LocalDate today = LocalDate.now();
        List<Inventory> expiredInventories = inventoryRepository.findByExpiryDateBefore(today);

        for (Inventory inventory : expiredInventories) {
            if (inventory.getStatus() != InventoryStatus.EXPIRED) {
                inventory.setStatus(InventoryStatus.EXPIRED);
                inventory.setLastUpdated(LocalDateTime.now());
            }
        }

        inventoryRepository.saveAll(expiredInventories);
    }
}
