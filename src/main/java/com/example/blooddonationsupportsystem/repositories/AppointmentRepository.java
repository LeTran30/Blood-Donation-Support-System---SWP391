package com.example.blooddonationsupportsystem.repositories;

import com.example.blooddonationsupportsystem.models.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Integer>,
        JpaSpecificationExecutor<Appointment> {

    List<Appointment> findAllByUserId(Integer userId);

}
