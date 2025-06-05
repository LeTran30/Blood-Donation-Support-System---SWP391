package com.example.blooddonationsupportsystem.repositories;

import com.example.blooddonationsupportsystem.models.EligibilityCheck;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EligibilityCheckRepository extends JpaRepository<EligibilityCheck, Integer> {
        EligibilityCheck findByUserId(int userId);
}
