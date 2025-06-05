package com.example.blooddonationsupportsystem.service.inventory;

import com.example.blooddonationsupportsystem.dtos.request.inventory.InventoryRequest;
import com.example.blooddonationsupportsystem.dtos.responses.inventory.InventoryResponse;

import java.util.List;

public interface IInventoryService {
    List<InventoryResponse> getAllInventory();
    InventoryResponse getInventoryById(Integer id);
    void createInventory(InventoryRequest request);
    void updateInventory(Integer id, InventoryRequest request);

}
