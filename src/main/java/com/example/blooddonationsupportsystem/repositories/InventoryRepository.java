package com.example.blooddonationsupportsystem.repositories;

import com.example.blooddonationsupportsystem.models.BloodComponent;
import com.example.blooddonationsupportsystem.models.BloodType;
import com.example.blooddonationsupportsystem.models.Inventory;
import com.example.blooddonationsupportsystem.utils.InventoryStatus;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Integer> {

    Optional<Inventory> findByBloodType(BloodType bloodType);

    List<Inventory> findByBloodTypeAndExpiryDateGreaterThanEqual(BloodType bloodType, LocalDate date);
    
    List<Inventory> findByExpiryDateBefore(LocalDate date);

    List<Inventory> findByBloodTypeAndBloodComponentAndStatus(BloodType bloodType, BloodComponent bloodComponent, InventoryStatus status);

    Optional<Inventory> findByBloodTypeAndBloodComponentAndBatchNumber(BloodType bloodType, BloodComponent component, @NotNull(message = "Batch number is required") String batchNumber);
}
