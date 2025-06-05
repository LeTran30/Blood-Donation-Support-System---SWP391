package com.example.blooddonationsupportsystem.repositories;

import com.example.blooddonationsupportsystem.models.BloodDonation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BloodDonationRepository extends JpaRepository<BloodDonation, Integer> {
}
