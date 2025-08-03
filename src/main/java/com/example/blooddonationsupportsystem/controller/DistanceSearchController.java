package com.example.blooddonationsupportsystem.controller;

import com.example.blooddonationsupportsystem.dtos.request.distanceSearch.DistanceSearchRequest;
import com.example.blooddonationsupportsystem.dtos.request.donorSearch.DonorSearchRequest;
import com.example.blooddonationsupportsystem.service.distanceSearch.IDistanceSearchService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
@RequestMapping("/api/v1/distance-search")
public class DistanceSearchController {

    private final IDistanceSearchService distanceSearchService;

    @Operation(summary = "Search for nearby blood donors", description = "Finds blood donors near the user's location with matching blood type and component.")
    @PostMapping("/api/distance-search")
    @PreAuthorize("true")
    public ResponseEntity<?> searchNearbyDonors(@RequestBody DistanceSearchRequest request) {
        return distanceSearchService.searchNearby(request);
    }

    @Operation(summary = "Get search history", description = "Returns previous nearby donor search history by user ID.")
    @GetMapping("/api/users/{userId}/distance-search")
    @PreAuthorize("true")
    public ResponseEntity<?> getSearchHistory(@PathVariable Integer userId) {
        return distanceSearchService.getSearchHistory(userId);
    }

    @PostMapping("/donor-nearby")
    @PreAuthorize("true")
    public ResponseEntity<?> searchDonorNearby(@RequestBody DonorSearchRequest request) {
        return distanceSearchService.searchDonorNearby(request);
    }
}