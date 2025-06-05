package com.example.blooddonationsupportsystem.repositories;

import com.example.blooddonationsupportsystem.models.DistanceSearch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DistanceSearchRepository extends JpaRepository<DistanceSearch, Long> {
    List<DistanceSearch> findAllByUserId(Integer userId);
}
