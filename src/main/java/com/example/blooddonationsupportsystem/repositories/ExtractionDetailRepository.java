package com.example.blooddonationsupportsystem.repositories;

import com.example.blooddonationsupportsystem.models.ExtractionDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExtractionDetailRepository extends JpaRepository<ExtractionDetail, Integer> {
    List<ExtractionDetail> findByExtraction_ExtractionId(Integer extractionId);

    void deleteByExtraction_ExtractionId(Integer id);
}