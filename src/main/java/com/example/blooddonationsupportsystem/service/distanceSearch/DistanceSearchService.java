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
import java.util.ArrayList;
import java.util.Comparator;
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
        // 1. Validate User
        User requester = userRepository.findById(request.getUserId()).orElse(null);
        if (requester == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ResponseObject.builder().status(HttpStatus.NOT_FOUND)
                            .message("User not found").build()
            );
        }

        // 2. Validate Blood Type
        BloodType bloodType = bloodTypeRepository.findById(request.getBloodTypeId()).orElse(null);
        if (bloodType == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ResponseObject.builder().status(HttpStatus.NOT_FOUND)
                            .message("Blood type not found").build()
            );
        }

        // 3. Lấy tất cả user có bloodTypeId khớp, bỏ qua bản thân requester
        List<User> matchedUsers = userRepository.findAllByBloodTypeId(bloodType.getBloodTypeId());

        List<DistanceSearchResponse> results = new ArrayList<>();

        for (User target : matchedUsers) {
            if (target.getId().equals(requester.getId())) continue;
            if (target.getLatitude() == null || target.getLongitude() == null) continue;

            double distanceKM = calculateDistance(
                    request.getLatitude(), request.getLongitude(),
                    target.getLatitude(), target.getLongitude()
            );

            // Ghi nhận tìm kiếm
            DistanceSearch record = DistanceSearch.builder()
                    .user(requester)
                    .targetUser(target)
                    .bloodType(bloodType)
                    .distanceKM(distanceKM)
                    .searchTime(Timestamp.valueOf(LocalDateTime.now()))
                    .build();

            distanceSearchRepository.save(record);

            DistanceSearchResponse response = modelMapper.map(record, DistanceSearchResponse.class);
            response.setTargetUserId(target.getId());
            response.setTargetUsername(target.getEmail()); // có thể thay bằng tên
            response.setDistanceKM(distanceKM);

            results.add(response);
        }

        // Sắp xếp kết quả tăng dần theo khoảng cách
        results.sort(Comparator.comparingDouble(DistanceSearchResponse::getDistanceKM));

        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.OK)
                .message("Nearby users found")
                .data(results)
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
                .map(ds -> {
                    DistanceSearchResponse res = modelMapper.map(ds, DistanceSearchResponse.class);
                    res.setTargetUserId(ds.getTargetUser().getId());
                    res.setTargetUsername(ds.getTargetUser().getEmail());
                    return res;
                })
                .toList();

        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.OK)
                .message("Search history retrieved successfully")
                .data(responseList)
                .build());
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371; // Earth radius in KM
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
