package com.example.blooddonationsupportsystem.service.healthCheck;


import com.example.blooddonationsupportsystem.dtos.request.healthCheck.HealthCheckRequest;
import com.example.blooddonationsupportsystem.dtos.responses.ResponseObject;
import com.example.blooddonationsupportsystem.dtos.responses.healthCheck.HealthCheckResponse;
import com.example.blooddonationsupportsystem.models.Appointment;
import com.example.blooddonationsupportsystem.models.BloodType;
import com.example.blooddonationsupportsystem.models.HealthCheck;
import com.example.blooddonationsupportsystem.models.User;
import com.example.blooddonationsupportsystem.repositories.AppointmentRepository;
import com.example.blooddonationsupportsystem.repositories.BloodTypeRepository;
import com.example.blooddonationsupportsystem.repositories.HealthCheckRepository;
import com.example.blooddonationsupportsystem.repositories.UserRepository;
import com.example.blooddonationsupportsystem.utils.AppointmentStatus;
import com.example.blooddonationsupportsystem.utils.Gender;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HealthCheckService implements IHealthCheckService {
    private final HealthCheckRepository healthCheckRepository;
    private final AppointmentRepository appointmentRepository;

    @Override
    @Transactional
    public ResponseEntity<?> createHealthCheck(HealthCheckRequest request) {
        try {
            // Find appointment
            Appointment appointment = appointmentRepository.findById(request.getAppointmentId())
                    .orElseThrow(() -> new RuntimeException("Appointment not found"));

            // Check status
            if (!appointment.getStatus().equals(AppointmentStatus.SCHEDULED)) {
                return ResponseEntity.badRequest().body(ResponseObject.builder()
                        .status(HttpStatus.BAD_REQUEST)
                        .message("Cannot perform health check on appointment not marked as SCHEDULED")
                        .build());
            }

            User user = appointment.getUser();
            if (Boolean.TRUE.equals(request.getIsEligible())) {
                Gender gender = user.getGender(); // giả sử là "MALE" hoặc "FEMALE"
                Double weight = request.getWeight();

                if ((gender == Gender.FEMALE) && weight < 42) {
                    return ResponseEntity.badRequest().body(ResponseObject.builder()
                            .status(HttpStatus.BAD_REQUEST)
                            .message("Females under 42kg are not eligible to donate blood.")
                            .build());
                }

                if ((gender == Gender.MALE) && weight < 45) {
                    return ResponseEntity.badRequest().body(ResponseObject.builder()
                            .status(HttpStatus.BAD_REQUEST)
                            .message("Males under 45kg are not eligible to donate blood.")
                            .build());
                }
            }

            HealthCheck healthCheck = HealthCheck.builder()
                    .user(user)
                    .appointment(appointment)
                    .pulse(request.getPulse())
                    .bloodPressure(request.getBloodPressure())
                    .resultSummary(request.getResultSummary())
                    .isEligible(request.getIsEligible())
                    .ineligibleReason(request.getIneligibleReason())
                    .weight(request.getWeight())
                    .suggestBloodVolume(request.getSuggestBloodVolume())
                    .checkedAt(LocalDateTime.now())
                    .build();

            healthCheckRepository.save(healthCheck);

            return ResponseEntity.ok(ResponseObject.builder()
                    .status(HttpStatus.OK)
                    .message("Health check created successfully")
                    .data(mapToResponse(healthCheck))
                    .build());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ResponseObject.builder()
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .message("Error: " + e.getMessage())
                            .build());
        }
    }

    @Override
    public ResponseEntity<?> getHealthChecksByUserId(Integer userId, int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("checkedAt").descending());
            Page<HealthCheck> healthChecksPage = healthCheckRepository.findByUserId(userId, pageable);

            List<HealthCheckResponse> responses = healthChecksPage.getContent().stream()
                    .map(hc -> HealthCheckResponse.builder()
                            .healthCheckId(hc.getId())
                            .pulse(hc.getPulse())
                            .bloodPressure(hc.getBloodPressure())
                            .resultSummary(hc.getResultSummary())
                            .checkedAt(hc.getCheckedAt())
                            .isEligible(hc.getIsEligible())
                            .ineligibleReason(hc.getIneligibleReason())
                            .build())
                    .toList();

            return ResponseEntity.ok(ResponseObject.builder()
                    .status(HttpStatus.OK)
                    .message("Health check records retrieved successfully")
                    .data(Map.of(
                            "content", responses,
                            "page", Map.of(
                                    "size", healthChecksPage.getSize(),
                                    "number", healthChecksPage.getNumber(),
                                    "totalElements", healthChecksPage.getTotalElements(),
                                    "totalPages", healthChecksPage.getTotalPages()
                            )
                    ))
                    .build());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseObject.builder()
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .message("Error: " + e.getMessage())
                            .build());
        }
    }


    @Override
    public ResponseEntity<?> getByAppointmentId(Integer appointmentId) {
        List<HealthCheck> healthChecks = healthCheckRepository.findByAppointment_AppointmentId(appointmentId);

        if (healthChecks.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ResponseObject.builder()
                            .status(HttpStatus.NOT_FOUND)
                            .message("No health checks found for appointment ID: " + appointmentId)
                            .build()
            );
        }

        List<HealthCheckResponse> responses = healthChecks.stream()
                .map(this::mapToResponse)
                .toList();

        return ResponseEntity.ok(
                ResponseObject.builder()
                        .status(HttpStatus.OK)
                        .message("Health checks fetched successfully")
                        .data(responses)
                        .build()
        );
    }


    @Override
    public ResponseEntity<?> getHealthCheckById(Integer healthCheckId) {
        Optional<HealthCheck> healthCheckOpt = healthCheckRepository.findById(healthCheckId);

        return healthCheckOpt.map(healthCheck -> ResponseEntity.ok(
                ResponseObject.builder()
                        .status(HttpStatus.OK)
                        .message("Health check fetched successfully")
                        .data(mapToResponse(healthCheck))
                        .build()
        )).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ResponseObject.builder()
                        .status(HttpStatus.NOT_FOUND)
                        .message("Health check not found with ID: " + healthCheckId)
                        .build()
        ));
    }

    private HealthCheckResponse mapToResponse(HealthCheck healthCheck) {
        HealthCheckResponse.HealthCheckResponseBuilder builder = HealthCheckResponse.builder()
                .healthCheckId(healthCheck.getId())
                .pulse(healthCheck.getPulse())
                .bloodPressure(healthCheck.getBloodPressure())
                .resultSummary(healthCheck.getResultSummary())
                .isEligible(healthCheck.getIsEligible())
                .ineligibleReason(healthCheck.getIneligibleReason())
                .weight(healthCheck.getWeight())
                .suggestBloodVolume(healthCheck.getSuggestBloodVolume())
                .checkedAt(healthCheck.getCheckedAt());
        return builder.build();
    }
}
