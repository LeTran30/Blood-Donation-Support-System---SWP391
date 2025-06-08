package com.example.blooddonationsupportsystem.repositories;

import com.example.blooddonationsupportsystem.models.BloodType;
import com.example.blooddonationsupportsystem.utils.BloodTypeName;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BloodTypeRepository extends JpaRepository<BloodType, Integer> {

    Optional<BloodType> findByTypeName(@NotNull BloodTypeName bloodType);
}
