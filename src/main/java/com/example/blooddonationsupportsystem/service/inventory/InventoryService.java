package com.example.blooddonationsupportsystem.service.inventory;

import com.example.blooddonationsupportsystem.dtos.request.inventory.InventoryRequest;
import com.example.blooddonationsupportsystem.dtos.responses.inventory.InventoryResponse;
import com.example.blooddonationsupportsystem.models.BloodComponent;
import com.example.blooddonationsupportsystem.models.BloodType;
import com.example.blooddonationsupportsystem.models.Inventory;
import com.example.blooddonationsupportsystem.repositories.BloodComponentRepository;
import com.example.blooddonationsupportsystem.repositories.BloodTypeRepository;
import com.example.blooddonationsupportsystem.repositories.InventoryRepository;
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
                .orElseThrow(() ->
                        new RuntimeException(
                                "Cannot find blood type with id"));

        BloodComponent bloodComponent = bloodComponentRepository.findById(request.getBloodType())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Cannot find blood components with id"));

        Inventory inventory = Inventory.builder()
                .bloodType(bloodType)
                .quantity(request.getQuantity())
                .bloodComponent(bloodComponent)
                .lastUpdated(LocalDateTime.now())
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
}
