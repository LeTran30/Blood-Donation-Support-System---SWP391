package com.example.blooddonationsupportsystem.repositories;

import com.example.blooddonationsupportsystem.models.BloodComponent;
import com.example.blooddonationsupportsystem.models.BloodType;
import com.example.blooddonationsupportsystem.models.Extract;
import com.example.blooddonationsupportsystem.models.Inventory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ExtractRepository extends JpaRepository<Extract, Integer> {
    
    List<Extract> findByInventory(Inventory inventory);
    
    List<Extract> findByBloodType(BloodType bloodType);
    
    List<Extract> findByBloodComponent(BloodComponent bloodComponent);
    
    List<Extract> findByBloodTypeAndBloodComponent(BloodType bloodType, BloodComponent bloodComponent);
    
    Page<Extract> findAll(Pageable pageable);
    
    List<Extract> findByExtractedAtBetween(LocalDateTime start, LocalDateTime end);
    
    List<Extract> findByBloodTypeAndExtractedAtBetween(BloodType bloodType, LocalDateTime start, LocalDateTime end);
    
    List<Extract> findByBloodComponentAndExtractedAtBetween(BloodComponent bloodComponent, LocalDateTime start, LocalDateTime end);
}