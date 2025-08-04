package com.example.blooddonationsupportsystem.service.inventory;

import com.example.blooddonationsupportsystem.dtos.request.inventory.InventoryRequest;
import com.example.blooddonationsupportsystem.dtos.request.inventory.InventoryUpdateRequest;
import com.example.blooddonationsupportsystem.dtos.responses.ResponseObject;
import com.example.blooddonationsupportsystem.dtos.responses.inventory.InventoryResponse;
import com.example.blooddonationsupportsystem.models.BloodDonation;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

public interface IInventoryService {
    Page<InventoryResponse> getAllInventory(int page, int size);
    InventoryResponse getInventoryById(Integer id);
    InventoryResponse createInventory(InventoryRequest request);
    InventoryResponse updateInventory(Integer id, InventoryUpdateRequest request);
    ResponseEntity<ResponseObject> deleteInventory(Integer id);
    
    List<InventoryResponse> findByBloodTypeAndNotExpired(Integer bloodTypeId, LocalDate date);
    List<InventoryResponse> findExpiredInventory(LocalDate date);

    @Transactional
    void updateInventoryAfterDonation(BloodDonation bloodDonation);
}
