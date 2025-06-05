package com.example.blooddonationsupportsystem.repositories;

import com.example.blooddonationsupportsystem.models.BloodRequestInventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BloodRequestInventoryRepository extends JpaRepository<BloodRequestInventory, Integer> {
    List<BloodRequestInventory> findByBloodRequestRequestId(Integer requestId);
}
