package com.example.blooddonationsupportsystem.service.inventory;

import com.example.blooddonationsupportsystem.dtos.request.inventory.InventoryRequest;
import com.example.blooddonationsupportsystem.dtos.responses.inventory.InventoryResponse;
import com.example.blooddonationsupportsystem.models.BloodDonation;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;

public interface IInventoryService {
    Page<InventoryResponse> getAllInventory(int page, int size);
    InventoryResponse getInventoryById(Integer id);
    InventoryResponse createInventory(InventoryRequest request);
    InventoryResponse updateInventory(Integer id, InventoryRequest request);

    @Transactional
    void updateInventoryAfterDonation(BloodDonation bloodDonation);
}
