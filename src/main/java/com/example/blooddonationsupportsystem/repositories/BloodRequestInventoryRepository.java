package com.example.blooddonationsupportsystem.repositories;

import com.example.blooddonationsupportsystem.models.BloodRequest;
import com.example.blooddonationsupportsystem.models.BloodRequestInventory;
import com.example.blooddonationsupportsystem.models.Inventory;
import com.example.blooddonationsupportsystem.utils.BloodRequestInventoryKey;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BloodRequestInventoryRepository extends JpaRepository<BloodRequestInventory, BloodRequestInventoryKey> {
    Page<BloodRequestInventory> findByBloodRequestRequestId(Integer requestId, Pageable pageable);

    Optional<BloodRequestInventory> findByBloodRequestAndInventory(BloodRequest bloodRequest, Inventory inventory);
}
