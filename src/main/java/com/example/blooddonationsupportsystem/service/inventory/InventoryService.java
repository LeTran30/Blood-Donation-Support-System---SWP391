package com.example.blooddonationsupportsystem.service.inventory;

import com.example.blooddonationsupportsystem.dtos.request.inventory.InventoryRequest;
import com.example.blooddonationsupportsystem.dtos.responses.inventory.InventoryResponse;
import com.example.blooddonationsupportsystem.models.Inventory;
import com.example.blooddonationsupportsystem.repositories.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryService implements IInventoryService {

    private final InventoryRepository inventoryRepository;

    @Override
    public List<InventoryResponse> getAllInventory() {
        return List.of();
    }

    @Override
    public InventoryResponse getInventoryById(Integer id) {
        return null;
    }

    @Override
    public void createInventory(InventoryRequest request) {
        Inventory inventory = Inventory.builder()
                .bloodType(request.getBloodType())
                .quantity(request.getQuantity())
                .bloodComponent(request.getBloodComponent())
                .lastUpdated(LocalDateTime.now())
                .build();
        inventoryRepository.save(inventory);
    }

    @Override
    public void updateInventory(Integer id, InventoryRequest request) {

    }
}
