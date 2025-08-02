package com.example.blooddonationsupportsystem.repositories;

import com.example.blooddonationsupportsystem.models.Appointment;
import com.example.blooddonationsupportsystem.models.HealthDeclaration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HealthDeclarationRepository extends JpaRepository<HealthDeclaration, Integer> {
    Optional<HealthDeclaration> findByAppointment(Appointment appointment);
    Optional<HealthDeclaration> findByAppointmentAppointmentId(Integer appointmentId);
}