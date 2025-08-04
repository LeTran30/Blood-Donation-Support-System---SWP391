package com.example.blooddonationsupportsystem.service.bloodDonationInformation;

import com.example.blooddonationsupportsystem.dtos.request.bloodDonationInfo.BloodDonationInformationRequest;
import com.example.blooddonationsupportsystem.dtos.responses.ResponseObject;
import com.example.blooddonationsupportsystem.dtos.responses.bloodDonationInfo.BloodDonationInformationResponse;
import com.example.blooddonationsupportsystem.models.Appointment;
import com.example.blooddonationsupportsystem.models.BloodDonationInformation;
import com.example.blooddonationsupportsystem.models.BloodType;
import com.example.blooddonationsupportsystem.models.User;
import com.example.blooddonationsupportsystem.repositories.AppointmentRepository;
import com.example.blooddonationsupportsystem.repositories.BloodDonationInformationRepository;
import com.example.blooddonationsupportsystem.repositories.BloodTypeRepository;
import com.example.blooddonationsupportsystem.repositories.UserRepository;
import com.example.blooddonationsupportsystem.service.certificate.ICertificateService;
import com.example.blooddonationsupportsystem.utils.AppointmentStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BloodDonationInformationService implements IBloodDonationInformationService {
    private final BloodDonationInformationRepository bloodDonationInformationRepository;
    private final AppointmentRepository appointmentRepository;
    private final BloodTypeRepository bloodTypeRepository;
    private final UserRepository userRepository;
    private final ICertificateService certificateService;

    @Override
    @Transactional
    public ResponseEntity<?> createBloodDonationInformation(BloodDonationInformationRequest request) {
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

            // Cho phép bloodTypeId = null
            BloodType bloodType = null;
            if (request.getBloodTypeId() != null) {
                Optional<BloodType> bloodTypeOpt = bloodTypeRepository.findById(request.getBloodTypeId());
                if (bloodTypeOpt.isEmpty()) {
                    return ResponseEntity.badRequest().body(
                            ResponseObject.builder()
                                    .status(HttpStatus.BAD_REQUEST)
                                    .message("Không tìm thấy nhóm máu")
                                    .build()
                    );
                }
                bloodType = bloodTypeOpt.get();

                // Chỉ auto-update bloodType cho user nếu user chưa có
                User user = appointment.getUser();
                if (user.getBloodType() == null) {
                    user.setBloodType(bloodType);
                    userRepository.save(user);
                }
            }
            appointment.setStatus(AppointmentStatus.MEDICAL_COMPLETED);
            BloodDonationInformation bloodDonationInformation = BloodDonationInformation.builder()
                    .appointment(appointment)
                    .bloodType(bloodType) // có thể null
                    .actualBloodVolume(request.getActualBloodVolume())
                    .build();
            if (bloodDonationInformationRepository.existsByAppointment(appointment)) {
                return ResponseEntity.badRequest().body(
                        ResponseObject.builder()
                                .status(HttpStatus.BAD_REQUEST)
                                .message("Thông tin hiến máu cho cuộc hẹn này đã tồn tại")
                                .build()
                );
            }
            appointmentRepository.save(appointment);
            BloodDonationInformation saved = bloodDonationInformationRepository.save(bloodDonationInformation);
            certificateService.generateCertificateForDonation(bloodDonationInformation.getBloodDonationInformationId());
            return ResponseEntity.ok(
                    ResponseObject.builder()
                            .status(HttpStatus.CREATED)
                            .message("Tạo thông tin hiến máu thành công")
                            .data(mapToResponseDTO(saved))
                            .build()
            );
        } catch (Exception e) {
            e.printStackTrace(); // hoặc dùng logger
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ResponseObject.builder()
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .message("Lỗi: " + e.getMessage())
                            .build()
            );
        }
    }

    @Override
    public ResponseEntity<?> getBloodDonationInformationByAppointmentId(Integer appointmentId) {
        try {
            Optional<BloodDonationInformation> bloodDonationInformationOpt = bloodDonationInformationRepository.findByAppointmentAppointmentId(appointmentId);
            return bloodDonationInformationOpt.map(bloodDonationInformation -> ResponseEntity.ok(
                    ResponseObject.builder()
                            .status(HttpStatus.OK)
                            .message("Truy xuất thông tin hiến máu thành công")
                            .data(mapToResponseDTO(bloodDonationInformation))
                            .build()
            )).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ResponseObject.builder()
                            .status(HttpStatus.NOT_FOUND)
                            .message("Không tìm thấy thông tin hiến máu cho cuộc hẹn có ID: " + appointmentId)
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
    public ResponseEntity<?> getBloodDonationInformationByUserId(Integer userId, int page, int size) {
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
            
            List<BloodDonationInformation> bloodDonationInformation = bloodDonationInformationRepository.findByUserOrderByCreateAtDesc(user);
            
            List<BloodDonationInformationResponse> responseList = bloodDonationInformation.stream()
                    .map(this::mapToResponseDTO)
                    .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("content", responseList);
            response.put("totalItems", responseList.size());
            response.put("totalPages", (int) Math.ceil((double) responseList.size() / size));
            response.put("currentPage", page);

            return ResponseEntity.ok(
                    ResponseObject.builder()
                            .status(HttpStatus.OK)
                            .message("Truy xuất thông tin hiến máu thành công")
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
    public ResponseEntity<?> getTotalBloodVolumeByUser(Integer userId) {
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
            Integer totalVolume = bloodDonationInformationRepository.getTotalBloodVolumeByUser(user);
            
            if (totalVolume == null) {
                totalVolume = 0;
            }

            Map<String, Object> response = new HashMap<>();
            response.put("userId", userId);
            response.put("totalBloodVolume", totalVolume);

            return ResponseEntity.ok(
                    ResponseObject.builder()
                            .status(HttpStatus.OK)
                            .message("Truy xuất tổng thể tích máu thành công")
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
    @Transactional
    public ResponseEntity<?> updateBloodDonationInformation(Integer id, BloodDonationInformationRequest request) {
        try {
            Optional<BloodDonationInformation> infoOpt = bloodDonationInformationRepository.findById(id);
            if (infoOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        ResponseObject.builder()
                                .status(HttpStatus.NOT_FOUND)
                                .message("Không tìm thấy thông tin hiến máu kèm ID: " + id)
                                .build()
                );
            }

            BloodDonationInformation info = infoOpt.get();
            BloodType bloodType = null;

            if (request.getBloodTypeId() != null) {
                Optional<BloodType> bloodTypeOpt = bloodTypeRepository.findById(request.getBloodTypeId());
                if (bloodTypeOpt.isEmpty()) {
                    return ResponseEntity.badRequest().body(
                            ResponseObject.builder()
                                    .status(HttpStatus.BAD_REQUEST)
                                    .message("Không tìm thấy nhóm máu")
                                    .build()
                    );
                }
                bloodType = bloodTypeOpt.get();

                // Auto update user blood type if not set
                User user = info.getAppointment().getUser();
                    user.setBloodType(bloodType);
                    userRepository.save(user);
            }

            info.setBloodType(bloodType); // null cũng được
            info.setActualBloodVolume(request.getActualBloodVolume());

            BloodDonationInformation updated = bloodDonationInformationRepository.save(info);

            return ResponseEntity.ok(
                    ResponseObject.builder()
                            .status(HttpStatus.OK)
                            .message("Cập nhập thông tin hiến máu thành công")
                            .data(mapToResponseDTO(updated))
                            .build()
            );
        } catch (Exception e) {
            e.printStackTrace(); // hoặc dùng logger
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ResponseObject.builder()
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .message("Lỗi: " + e.getMessage())
                            .build()
            );
        }
    }

    @Override
    @Transactional
    public ResponseEntity<?> deleteBloodDonationInformation(Integer id) {
        try {
            Optional<BloodDonationInformation> infoOpt = bloodDonationInformationRepository.findById(id);
            if (infoOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        ResponseObject.builder()
                                .status(HttpStatus.NOT_FOUND)
                                .message("Không tìm thấy thông tin hiến máu kèm ID: " + id)
                                .build()
                );
            }

            bloodDonationInformationRepository.delete(infoOpt.get());

            return ResponseEntity.ok(
                    ResponseObject.builder()
                            .status(HttpStatus.OK)
                            .message("Xóa thông tin hiến máu thành công")
                            .build()
            );
        } catch (Exception e) {
            e.printStackTrace(); // hoặc dùng logger
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ResponseObject.builder()
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .message("Lỗi: " + e.getMessage())
                            .build()
            );
        }
    }

    @Override
    public ResponseEntity<?> getAllBloodDonationInformation(int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("createAt").descending());
            var infosPage = bloodDonationInformationRepository.findAll(pageable);

            List<BloodDonationInformationResponse> content = infosPage.getContent().stream()
                    .map(this::mapToResponseDTO)
                    .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("content", content);
            response.put("totalItems", infosPage.getTotalElements());
            response.put("totalPages", infosPage.getTotalPages());
            response.put("currentPage", infosPage.getNumber());

            return ResponseEntity.ok(
                    ResponseObject.builder()
                            .status(HttpStatus.OK)
                            .message("Truy xuất tất cả thông tin hiến máu thành công")
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


    private BloodDonationInformationResponse mapToResponseDTO(BloodDonationInformation bloodDonationInformation) {
        User user = bloodDonationInformation.getAppointment().getUser();
        BloodType bloodType = bloodDonationInformation.getBloodType();

        return BloodDonationInformationResponse.builder()
                .bloodDonationInformationId(bloodDonationInformation.getBloodDonationInformationId())
                .appointmentId(bloodDonationInformation.getAppointment().getAppointmentId())
                .bloodTypeId(bloodType != null ? bloodType.getBloodTypeId() : null)
                .bloodTypeName(bloodType != null ? bloodType.getTypeName() : null)
                .actualBloodVolume(bloodDonationInformation.getActualBloodVolume())
                .createdAt(bloodDonationInformation.getCreateAt())
                .updatedAt(bloodDonationInformation.getUpdateAt())
                .userId(user.getId())
                .userName(user.getFullName())
                .build();
    }


}