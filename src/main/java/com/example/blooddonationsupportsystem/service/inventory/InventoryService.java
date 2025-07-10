package com.example.blooddonationsupportsystem.service.inventory;

import com.example.blooddonationsupportsystem.dtos.request.inventory.InventoryRequest;
import com.example.blooddonationsupportsystem.dtos.responses.inventory.InventoryResponse;
import com.example.blooddonationsupportsystem.models.BloodComponent;
import com.example.blooddonationsupportsystem.models.BloodDonation;
import com.example.blooddonationsupportsystem.models.BloodType;
import com.example.blooddonationsupportsystem.models.Inventory;
import com.example.blooddonationsupportsystem.repositories.BloodComponentRepository;
import com.example.blooddonationsupportsystem.repositories.BloodTypeRepository;
import com.example.blooddonationsupportsystem.repositories.InventoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InventoryService implements IInventoryService {

    private final InventoryRepository inventoryRepository;
    private final BloodComponentRepository bloodComponentRepository;
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
                        .bloodComponentId(inventory.getBloodComponent().getComponentId())
                        .quantity(inventory.getQuantity())
                        .lastUpdated(inventory.getLastUpdated())
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
                .bloodComponentId(inventory.getBloodComponent().getComponentId())
                .quantity(inventory.getQuantity())
                .lastUpdated(inventory.getLastUpdated())
                .build();
    }

    @Override
    public InventoryResponse createInventory(InventoryRequest request) {
        BloodType bloodType = bloodTypeRepository.findById(request.getBloodTypeId())
                .orElseThrow(() -> new RuntimeException("Cannot find blood type with id: " + request.getBloodTypeId()));

        BloodComponent bloodComponent = bloodComponentRepository.findById(request.getBloodComponentId())
                .orElseThrow(() -> new RuntimeException("Cannot find blood component with id: " + request.getBloodComponentId()));

        // Tìm xem inventory đã tồn tại chưa
        Optional<Inventory> optionalInventory = inventoryRepository.findByBloodTypeAndBloodComponent(bloodType, bloodComponent);

        Inventory inventory;
        if (optionalInventory.isPresent()) {
            // Nếu tồn tại, cộng dồn số lượng
            inventory = optionalInventory.get();
            inventory.setQuantity(inventory.getQuantity() + request.getQuantity());
        } else {
            // Nếu chưa tồn tại, tạo mới
            inventory = Inventory.builder()
                    .bloodType(bloodType)
                    .bloodComponent(bloodComponent)
                    .quantity(request.getQuantity())
                    .build();
        }

        inventoryRepository.save(inventory);

        return InventoryResponse.builder()
                .id(inventory.getInventoryId())
                .bloodTypeId(inventory.getBloodType().getBloodTypeId())
                .bloodComponentId(inventory.getBloodComponent().getComponentId())
                .quantity(inventory.getQuantity())
                .lastUpdated(inventory.getLastUpdated())
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

        BloodComponent bloodComponent = bloodComponentRepository.findById(request.getBloodComponentId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Cannot find blood component with id: " + request.getBloodComponentId()));

        inventory.setBloodType(bloodType);
        inventory.setBloodComponent(bloodComponent);
        inventory.setQuantity(request.getQuantity());
        inventory.setLastUpdated(LocalDateTime.now());
        inventoryRepository.save(inventory);
        return InventoryResponse.builder()
                .id(inventory.getInventoryId())
                .bloodTypeId(inventory.getBloodType().getBloodTypeId())
                .bloodComponentId(inventory.getBloodComponent().getComponentId())
                .quantity(inventory.getQuantity())
                .lastUpdated(inventory.getLastUpdated())
                .build();
    }

    @Transactional
    @Override
    public void updateInventoryAfterDonation(BloodDonation bloodDonation) {
        BloodType bloodType = bloodDonation.getBloodType();
        BloodComponent bloodComponent = bloodDonation.getBloodComponent();
        Integer volume = bloodDonation.getVolumeMl();

        Inventory inventory = inventoryRepository.findByBloodTypeAndBloodComponent(bloodType, bloodComponent)
                .orElseGet(() -> {
                    Inventory newInventory = Inventory.builder()
                            .bloodType(bloodType)
                            .bloodComponent(bloodComponent)
                            .quantity(0)
                            .lastUpdated(LocalDateTime.now())
                            .build();
                    return inventoryRepository.save(newInventory);
                });
        inventory.setQuantity(inventory.getQuantity() + volume);
        inventory.setLastUpdated(LocalDateTime.now());

        inventoryRepository.save(inventory);
    }
}
