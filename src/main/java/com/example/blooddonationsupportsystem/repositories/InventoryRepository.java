package com.example.blooddonationsupportsystem.repositories;

import com.example.blooddonationsupportsystem.models.BloodComponent;
import com.example.blooddonationsupportsystem.models.BloodType;
import com.example.blooddonationsupportsystem.models.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Integer> {

    Optional<Inventory> findByBloodType(BloodType bloodType);

    List<Inventory> findByBloodTypeAndExpiryDateGreaterThanEqual(BloodType bloodType, LocalDate date);
    
    List<Inventory> findByExpiryDateBefore(LocalDate date);

    List<Inventory> findByBloodTypeAndBloodComponentAndExpiryDateGreaterThanEqual(BloodType bloodType, BloodComponent bloodComponent, LocalDate now);
}
