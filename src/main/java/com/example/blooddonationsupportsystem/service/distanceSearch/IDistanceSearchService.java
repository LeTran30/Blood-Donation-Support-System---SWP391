package com.example.blooddonationsupportsystem.service.distanceSearch;

import com.example.blooddonationsupportsystem.dtos.request.distanceSearch.DistanceSearchRequest;
import org.springframework.http.ResponseEntity;

public interface IDistanceSearchService {
    ResponseEntity<?> searchNearby(DistanceSearchRequest request);
    ResponseEntity<?> getSearchHistory(Integer userId);
}
