package com.example.blooddonationsupportsystem.repositories;

import com.example.blooddonationsupportsystem.models.HealthCheck;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HealthCheckRepository extends JpaRepository<HealthCheck, Integer> {
}
