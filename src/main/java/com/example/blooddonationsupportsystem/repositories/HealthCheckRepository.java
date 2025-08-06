package com.example.blooddonationsupportsystem.repositories;

import com.example.blooddonationsupportsystem.models.HealthCheck;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HealthCheckRepository extends JpaRepository<HealthCheck, Integer> {
    List<HealthCheck> findByAppointment_AppointmentId(Integer appointmentId);

    Page<HealthCheck> findByUserId(Integer userId, Pageable pageable);
}
