package com.example.blooddonationsupportsystem.service.distanceSearch;

import com.example.blooddonationsupportsystem.dtos.request.distanceSearch.DistanceSearchRequest;
import com.example.blooddonationsupportsystem.dtos.responses.ResponseObject;
import com.example.blooddonationsupportsystem.dtos.responses.distanceSearch.DistanceSearchResponse;
import com.example.blooddonationsupportsystem.models.BloodType;
import com.example.blooddonationsupportsystem.models.DistanceSearch;
import com.example.blooddonationsupportsystem.models.User;
import com.example.blooddonationsupportsystem.repositories.BloodTypeRepository;
import com.example.blooddonationsupportsystem.repositories.DistanceSearchRepository;
import com.example.blooddonationsupportsystem.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
@Service
@RequiredArgsConstructor
public class DistanceSearchService implements IDistanceSearchService {

    private final DistanceSearchRepository distanceSearchRepository;
    private final UserRepository userRepository;
    private final BloodTypeRepository bloodTypeRepository;
    private final ModelMapper modelMapper;

    @Override
    public ResponseEntity<?> searchNearby(DistanceSearchRequest request) {
        // Validate user exists
        Optional<User> userOpt = userRepository.findById(request.getUserId());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseObject.builder()
                            .status(HttpStatus.NOT_FOUND)
                            .message("User not found")
                            .build());
        }
        User user = userOpt.get();

        // Validate blood type exists
        Optional<BloodType> bloodTypeOpt = bloodTypeRepository.findById(request.getBloodTypeId());
        if (bloodTypeOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseObject.builder()
                            .status(HttpStatus.NOT_FOUND)
                            .message("Blood type not found")
                            .build());
        }
        BloodType bloodType = bloodTypeOpt.get();

        // For demo, just save the search record with dummy targetUser and distance
        // In real app, you'd do spatial query or business logic here

        User targetUser = userRepository.findById(request.getTargetUserId())
                .orElse(null);
        if (targetUser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseObject.builder()
                            .status(HttpStatus.NOT_FOUND)
                            .message("Target user not found")
                            .build());
        }

        DistanceSearch distanceSearch = DistanceSearch.builder()
                .user(user)
                .targetUser(targetUser)
                .bloodType(bloodType)
                .distanceKM(request.getDistanceKM())
                .searchTime(Timestamp.valueOf(LocalDateTime.now()))
                .build();

        distanceSearchRepository.save(distanceSearch);

        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.OK)
                .message("Search saved successfully")
                .data(modelMapper.map(distanceSearch, DistanceSearchResponse.class))
                .build());
    }

    @Override
    public ResponseEntity<?> getSearchHistory(Integer userId) {
        if (!userRepository.existsById(userId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseObject.builder()
                            .status(HttpStatus.NOT_FOUND)
                            .message("User not found")
                            .build());
        }

        List<DistanceSearch> searches = distanceSearchRepository.findAllByUserId(userId);

        List<DistanceSearchResponse> responseList = searches.stream()
                .map(ds -> modelMapper.map(ds, DistanceSearchResponse.class))
                .toList();

        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.OK)
                .message("Search history retrieved successfully")
                .data(responseList)
                .build());
    }
}
