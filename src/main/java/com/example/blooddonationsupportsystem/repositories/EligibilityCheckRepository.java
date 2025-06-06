package com.example.blooddonationsupportsystem.repositories;

import com.example.blooddonationsupportsystem.models.EligibilityCheck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EligibilityCheckRepository extends JpaRepository<EligibilityCheck, Integer> {
        @Query("SELECT e FROM EligibilityCheck e WHERE e.user.id = :userId")
        EligibilityCheck findByUserId(@Param("userId") int userId);
}
