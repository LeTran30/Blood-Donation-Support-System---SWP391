package com.example.blooddonationsupportsystem.service.extraction;

import com.example.blooddonationsupportsystem.dtos.request.extraction.ExtractionRequest;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;

public interface IExtractionService {
    @Transactional
    ResponseEntity<?> createExtraction(ExtractionRequest request);

    ResponseEntity<?> getExtractionById(Integer id);

    ResponseEntity<?> getAllExtractions(Integer bloodTypeId, Integer bloodComponentId, int page, int size);

    ResponseEntity<?> getExtractionDetailsByExtractionId(Integer extractionId);

    @Transactional
    ResponseEntity<?> deleteExtraction(Integer extractionId);

    @Transactional
    ResponseEntity<?> updateExtraction(Integer id, ExtractionRequest request);
}
