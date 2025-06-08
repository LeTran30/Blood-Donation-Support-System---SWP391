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
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryService implements IInventoryService {

    private final InventoryRepository inventoryRepository;
    private final BloodComponentRepository bloodComponentRepository;
    private final BloodTypeRepository bloodTypeRepository;

    @Override
    public List<InventoryResponse> getAllInventory() {
        List<Inventory> inventoryList = inventoryRepository.findAll();
        return inventoryList
                .stream()
                .map(inventory -> InventoryResponse.builder()
                        .id(inventory.getInventoryId())
                        .bloodType(inventory.getBloodType().getBloodTypeId())
                        .quantity(inventory.getQuantity())
                        .lastUpdated(inventory.getLastUpdated())
                        .build())
                .toList();
    }

    @Override
    public InventoryResponse getInventoryById(Integer id) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cannot find inventory with id: " + id));
        return InventoryResponse.builder()
                .id(inventory.getInventoryId())
                .bloodType(inventory.getBloodType().getBloodTypeId())
                .quantity(inventory.getQuantity())
                .lastUpdated(inventory.getLastUpdated())
                .build();
    }

    @Override
    public void createInventory(InventoryRequest request) {
        BloodType bloodType = bloodTypeRepository.findById(request.getBloodType())
                .orElseThrow(() -> new RuntimeException("Cannot find blood type with id: " + request.getBloodType()));

        BloodComponent bloodComponent = bloodComponentRepository.findById(request.getBloodComponent())
                .orElseThrow(() -> new RuntimeException("Cannot find blood component with id: " + request.getBloodComponent()));

        Inventory inventory = Inventory.builder()
                .bloodType(bloodType)
                .bloodComponent(bloodComponent)
                .quantity(request.getQuantity())
                .build();
        inventoryRepository.save(inventory);
    }


    @Override
    public void updateInventory(Integer id, InventoryRequest request) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cannot find inventory with id: " + id));

        BloodType bloodType = bloodTypeRepository.findById(request.getBloodType())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Cannot find blood type with id: " + request.getBloodType()));

        BloodComponent bloodComponent = bloodComponentRepository.findById(request.getBloodComponent())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Cannot find blood component with id: " + request.getBloodComponent()));

        inventory.setBloodType(bloodType);
        inventory.setBloodComponent(bloodComponent);
        inventory.setQuantity(request.getQuantity());
        inventory.setLastUpdated(LocalDateTime.now());
        inventoryRepository.save(inventory);
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
