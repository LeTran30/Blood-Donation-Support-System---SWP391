package com.example.blooddonationsupportsystem.repositories;

import com.example.blooddonationsupportsystem.models.Appointment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Integer>,
        JpaSpecificationExecutor<Appointment> {

    Page<Appointment> findAllByUserId(Integer userId,
                                      Pageable pageable);

    Appointment findTopByUserIdOrderByAppointmentDateDesc(Integer id);
}
