package com.example.blooddonationsupportsystem.service.healthDeclaration;

import com.example.blooddonationsupportsystem.dtos.request.healthDeclaration.HealthDeclarationRequest;
import com.example.blooddonationsupportsystem.dtos.request.healthDeclaration.HealthDeclarationUpdateRequest;
import com.example.blooddonationsupportsystem.dtos.responses.ResponseObject;
import com.example.blooddonationsupportsystem.dtos.responses.healthDeclaration.HealthDeclarationResponse;
import com.example.blooddonationsupportsystem.models.Appointment;
import com.example.blooddonationsupportsystem.models.HealthDeclaration;
import com.example.blooddonationsupportsystem.models.User;
import com.example.blooddonationsupportsystem.repositories.AppointmentRepository;
import com.example.blooddonationsupportsystem.repositories.HealthDeclarationRepository;
import com.example.blooddonationsupportsystem.repositories.UserRepository;
import com.example.blooddonationsupportsystem.utils.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HealthDeclarationService implements IHealthDeclarationService {
    private final HealthDeclarationRepository healthDeclarationRepository;
    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;

    @Override
    public ResponseEntity<?> createHealthDeclaration(HealthDeclarationRequest request, Integer userId) {
        try {
            Optional<Appointment> appointmentOpt = appointmentRepository.findById(request.getAppointmentId());
            if (appointmentOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(
                        ResponseObject.builder()
                                .status(HttpStatus.BAD_REQUEST)
                                .message("Không tìm thấy cuộc hẹn")
                                .build()
                );
            }

            Appointment appointment = appointmentOpt.get();
            if (!appointment.getUser().getId().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                        ResponseObject.builder()
                                .status(HttpStatus.FORBIDDEN)
                                .message("Bạn không có quyền để tạo bản kê khai y tế cho cuộc hẹn này")
                                .build()
                );
            }
            // Check if declaration already exists
            if (healthDeclarationRepository.findByAppointment(appointment).isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(
                        ResponseObject.builder()
                                .status(HttpStatus.CONFLICT)
                                .message("Đã có bản kê khai y tế cho cuộc hẹn này")
                                .build()
                );
            }

            HealthDeclaration declaration = HealthDeclaration.builder()
                    .appointment(appointment)
                    .hasBloodTransmittedDisease(request.getHasBloodTransmittedDisease())
                    .hasChronicDisease(request.getHasChronicDisease())
                    .currentMedications(request.getCurrentMedications())
                    .hasTattooAcupuncture(request.getHasTattooAcupuncture())
                    .hasRecentVaccine(request.getHasRecentVaccine())
                    .hasTravelAbroad(request.getHasTravelAbroad())
                    .hasUnsafeSex(request.getHasUnsafeSex())
                    .isFirstBlood(request.getIsFirstBlood())
                    .isPregnantOrBreastfeeding(request.getIsPregnantOrBreastfeeding())
                    .isMenstruating(request.getIsMenstruating())
                    .build();

            HealthDeclaration saved = healthDeclarationRepository.save(declaration);

            return ResponseEntity.ok(
                    ResponseObject.builder()
                            .status(HttpStatus.CREATED)
                            .message("Tạo các bản kê khai y tế thành công")
                            .data(mapToResponseDTO(saved))
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ResponseObject.builder()
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .message("Lỗi: " + e.getMessage())
                            .build()
            );
        }
    }


    @Override
    public ResponseEntity<?> getHealthDeclarationByAppointmentId(Integer appointmentId) {
        try {
            Optional<HealthDeclaration> healthDeclarationOpt = healthDeclarationRepository.findByAppointmentAppointmentId(appointmentId);
            return healthDeclarationOpt.map(healthDeclaration -> ResponseEntity.ok(
                    ResponseObject.builder()
                            .status(HttpStatus.OK)
                            .message("Truy xuất bản kê khai y tế thành công")
                            .data(mapToResponseDTO(healthDeclaration))
                            .build()
            )).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ResponseObject.builder()
                            .status(HttpStatus.NOT_FOUND)
                            .message("Không tìm thấy bản kê khai y tế với cuộc hẹn với ID: " + appointmentId)
                            .build()
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ResponseObject.builder()
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .message("Lỗi: " + e.getMessage())
                            .build()
            );
        }
    }

    @Override
    public ResponseEntity<?> getHealthDeclarationsByUserId(Integer userId, int page, int size) {
        try {
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        ResponseObject.builder()
                                .status(HttpStatus.NOT_FOUND)
                                .message("Không tìm thấy người dùng")
                                .build()
                );
            }

            User user = userOpt.get();
            Pageable pageable = PageRequest.of(page, size, Sort.by("createAt").descending());
            
            // Get all appointments for the user
            Page<Appointment> appointments = appointmentRepository.findAllByUserId(userId, pageable);
            
            // Get health declarations for each appointment
            List<HealthDeclarationResponse> responseList = appointments.getContent().stream()
                    .map(appointment -> {
                        Optional<HealthDeclaration> healthDeclarationOpt = healthDeclarationRepository.findByAppointment(appointment);
                        return healthDeclarationOpt.map(this::mapToResponseDTO).orElse(null);
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("content", responseList);
            response.put("totalItems", appointments.getTotalElements());
            response.put("totalPages", appointments.getTotalPages());
            response.put("currentPage", page);

            return ResponseEntity.ok(
                    ResponseObject.builder()
                            .status(HttpStatus.OK)
                            .message("Truy xuất các bản kê khai y tế thành công")
                            .data(response)
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ResponseObject.builder()
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .message("Lỗi: " + e.getMessage())
                            .build()
            );
        }
    }
    @Override
    public ResponseEntity<?> updateHealthDeclaration(Integer healthDeclarationId, HealthDeclarationUpdateRequest request, Integer userId, Role role) {
        try {
            Optional<HealthDeclaration> declarationOpt = healthDeclarationRepository.findById(healthDeclarationId);
            if (declarationOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        ResponseObject.builder()
                                .status(HttpStatus.NOT_FOUND)
                                .message("Không tìm thấy bản kê khai y tế kèm ID.: " + healthDeclarationId)
                                .build()
                );
            }

            HealthDeclaration declaration = declarationOpt.get();
            Appointment appointment = declaration.getAppointment();

            if (role == Role.MEMBER && !appointment.getUser().getId().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                        ResponseObject.builder()
                                .status(HttpStatus.FORBIDDEN)
                                .message("Tài khoản của bạn không có quyền cần thiết để chỉnh sửa bản kê khai y tế.")
                                .build()
                );
            }

            declaration.setHasBloodTransmittedDisease(request.getHasBloodTransmittedDisease());
            declaration.setHasChronicDisease(request.getHasChronicDisease());
            declaration.setCurrentMedications(request.getCurrentMedications());
            declaration.setHasTattooAcupuncture(request.getHasTattooAcupuncture());
            declaration.setHasRecentVaccine(request.getHasRecentVaccine());
            declaration.setHasTravelAbroad(request.getHasTravelAbroad());
            declaration.setHasUnsafeSex(request.getHasUnsafeSex());
            declaration.setIsFirstBlood(request.getIsFirstBlood());
            declaration.setIsPregnantOrBreastfeeding(request.getIsPregnantOrBreastfeeding());
            declaration.setIsMenstruating(request.getIsMenstruating());

            HealthDeclaration updated = healthDeclarationRepository.save(declaration);

            return ResponseEntity.ok(
                    ResponseObject.builder()
                            .status(HttpStatus.OK)
                            .message("Cập nhật bản kê khai y tế thành công")
                            .data(mapToResponseDTO(updated))
                            .build()
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ResponseObject.builder()
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .message("Lỗi: " + e.getMessage())
                            .build()
            );
        }
    }

    @Override
    public ResponseEntity<?> getHealthDeclarationById(Integer id) {
        Optional<HealthDeclaration> declarationOpt = healthDeclarationRepository.findById(id);
        return declarationOpt.map(healthDeclaration -> ResponseEntity.ok(
                ResponseObject.builder()
                        .status(HttpStatus.OK)
                        .message("Truy xuất bản kê khai y tế thành công")
                        .data(mapToResponseDTO(healthDeclaration))
                        .build()
        )).orElseGet(() -> ResponseEntity.ok(
                ResponseObject.builder()
                        .status(HttpStatus.NOT_FOUND)
                        .message("Không tìm thấy bản kê khai y tế kèm ID.: " + id)
                        .build()
        ));

    }

    @Override
    public ResponseEntity<?> deleteHealthDeclaration(Integer healthDeclarationId) {
        try {
            Optional<HealthDeclaration> declarationOpt = healthDeclarationRepository.findById(healthDeclarationId);
            if (declarationOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        ResponseObject.builder()
                                .status(HttpStatus.NOT_FOUND)
                                .message("Không tìm thấy bản kê khai y tế kèm ID.: " + healthDeclarationId)
                                .build()
                );
            }

            healthDeclarationRepository.deleteById(healthDeclarationId);

            return ResponseEntity.ok(
                    ResponseObject.builder()
                            .status(HttpStatus.OK)
                            .message("Xóa bản kê khai y tế thành công")
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ResponseObject.builder()
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .message("Lỗi: " + e.getMessage())
                            .build()
            );
        }
    }


    private HealthDeclarationResponse mapToResponseDTO(HealthDeclaration healthDeclaration) {
        User user = healthDeclaration.getAppointment().getUser();

        return HealthDeclarationResponse.builder()
                .healthDeclarationId(healthDeclaration.getHealthDeclarationId())
                .appointmentId(healthDeclaration.getAppointment().getAppointmentId())
                .hasBloodTransmittedDisease(healthDeclaration.getHasBloodTransmittedDisease())
                .hasChronicDisease(healthDeclaration.getHasChronicDisease())
                .currentMedications(healthDeclaration.getCurrentMedications())
                .hasTattooAcupuncture(healthDeclaration.getHasTattooAcupuncture())
                .hasRecentVaccine(healthDeclaration.getHasRecentVaccine())
                .hasTravelAbroad(healthDeclaration.getHasTravelAbroad())
                .hasUnsafeSex(healthDeclaration.getHasUnsafeSex())
                .isFirstBlood(healthDeclaration.getIsFirstBlood())
                .isPregnantOrBreastfeeding(healthDeclaration.getIsPregnantOrBreastfeeding())
                .isMenstruating(healthDeclaration.getIsMenstruating())
                .userId(user.getId())
                .userName(user.getFullName())
                .createdAt(healthDeclaration.getCreateAt())
                .updatedAt(healthDeclaration.getUpdateAt())
                .build();
    }

}