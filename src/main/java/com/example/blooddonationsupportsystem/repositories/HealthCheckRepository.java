package com.example.blooddonationsupportsystem.repositories;

import com.example.blooddonationsupportsystem.models.HealthCheck;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HealthCheckRepository extends JpaRepository<HealthCheck, Integer> {
    Optional<HealthCheck> findByAppointment_AppointmentId(Integer appointmentId);

    List<HealthCheck> findByUserId(Integer userId);
}
