package com.example.blooddonationsupportsystem.repositories;

import com.example.blooddonationsupportsystem.models.BloodComponent;
import com.example.blooddonationsupportsystem.models.BloodType;
import com.example.blooddonationsupportsystem.models.Extraction;
import com.example.blooddonationsupportsystem.models.Inventory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExtractionRepository extends JpaRepository<Extraction, Integer> {
    
    Page<Extraction> findAll(Pageable pageable);

    Page<Extraction> findByBloodType_BloodTypeIdAndBloodComponent_ComponentId(Integer bloodTypeId, Integer bloodComponentId, Pageable pageable);

    Page<Extraction> findByBloodType_BloodTypeId(Integer bloodTypeId, Pageable pageable);

    Page<Extraction> findByBloodComponent_ComponentId(Integer bloodComponentId, Pageable pageable);
}