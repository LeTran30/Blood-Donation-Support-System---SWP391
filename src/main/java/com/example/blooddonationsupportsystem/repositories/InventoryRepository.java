package com.example.blooddonationsupportsystem.repositories;

import com.example.blooddonationsupportsystem.models.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRepository extends JpaRepository<Inventory, Integer> {
    
}
