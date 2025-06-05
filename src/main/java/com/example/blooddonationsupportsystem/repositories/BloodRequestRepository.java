package com.example.blooddonationsupportsystem.repositories;

import com.example.blooddonationsupportsystem.models.BloodRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BloodRequestRepository extends JpaRepository<BloodRequest, Long> {

    Optional<BloodRequest> findByRequestId(Integer requestId);
}
