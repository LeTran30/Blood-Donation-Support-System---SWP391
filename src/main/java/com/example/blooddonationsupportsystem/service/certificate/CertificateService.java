package com.example.blooddonationsupportsystem.service.certificate;

import com.example.blooddonationsupportsystem.dtos.responses.ResponseObject;
import com.example.blooddonationsupportsystem.dtos.responses.certificate.CertificateResponse;
import com.example.blooddonationsupportsystem.models.BloodDonationInformation;
import com.example.blooddonationsupportsystem.models.Certificate;
import com.example.blooddonationsupportsystem.models.HealthDeclaration;
import com.example.blooddonationsupportsystem.models.User;
import com.example.blooddonationsupportsystem.repositories.BloodDonationInformationRepository;
import com.example.blooddonationsupportsystem.repositories.CertificateRepository;
import com.example.blooddonationsupportsystem.repositories.UserRepository;
import com.example.blooddonationsupportsystem.utils.CertificateType;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CertificateService implements ICertificateService {
    private final CertificateRepository certificateRepository;
    private final BloodDonationInformationRepository bloodDonationInformationRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public CertificateResponse generateCertificateForDonation(Integer bloodDonationInforId) {
        BloodDonationInformation bloodDonationInformation = bloodDonationInformationRepository
                .findById(bloodDonationInforId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thông tin hiến máu"));

        User user = bloodDonationInformation.getAppointment().getUser();
        int volume = bloodDonationInformation.getActualBloodVolume();

        // Tổng lượng máu sau khi hiến
        Integer totalVolume = bloodDonationInformationRepository.getTotalBloodVolumeByUser(user);
        if (totalVolume == null) totalVolume = volume;

        // Trước khi hiến
        int totalBefore = totalVolume - volume;

        // Số lần hiến
        long donationCount = bloodDonationInformationRepository.countByAppointment_User(user);
        boolean isFirstDonation = donationCount == 0;

        // Tính số năm đã hiến máu
        LocalDate firstDonationDate = bloodDonationInformationRepository
                .findEarliestDonationDateByUser(user)
                .orElse(LocalDate.now());
        long yearsOfDonation = ChronoUnit.YEARS.between(firstDonationDate, LocalDate.now());

        // Xác định loại chứng nhận
        CertificateType certificateType = CertificateType.CERTIFICATE;
        String meritReasonDescription = null;

        // 1. Đạt mốc tổng lượng máu
        List<Integer> volumeMilestones = Arrays.asList(1000, 2000, 3000, 5000);
        for (Integer milestone : volumeMilestones) {
            if (totalBefore < milestone && totalVolume >= milestone) {
                certificateType = CertificateType.MERIT;
                meritReasonDescription = String.format("Bạn vừa đạt mốc hiến máu tổng cộng %d ml – một thành tích đáng trân trọng. ", milestone);
                break;
            }
        }

        // 2. Đạt mốc số lần hiến
        List<Integer> donationMilestones = Arrays.asList(5, 10, 20, 30);
        for (Integer milestone : donationMilestones) {
            if ((donationCount - 1) < milestone && donationCount >= milestone) {
                certificateType = CertificateType.MERIT;
                meritReasonDescription = String.format("Bạn đã hiến máu %d lần – một tinh thần bền bỉ thật đáng ngưỡng mộ. ", milestone);
                break;
            }
        }

        // 3. Hiến máu đều đặn nhiều năm
        if (donationCount >= 10 && yearsOfDonation >= 5) {
            certificateType = CertificateType.MERIT;
            meritReasonDescription = String.format("Bạn đã hiến máu đều đặn trong hơn %d năm – một tấm gương cống hiến lâu dài cho cộng đồng. ", yearsOfDonation);
        }

        String donationDate = bloodDonationInformation.getAppointment().getAppointmentDate()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        // Mô tả giấy chứng nhận
        StringBuilder description = new StringBuilder();
        description.append(String.format("Chứng nhận hiến máu vào ngày %s. ", donationDate));
        description.append(String.format("%s đã hiến %d ml máu. ", user.getFullName(), volume));

        if (isFirstDonation) {
            description.append("Đây là lần đầu tiên tham gia hiến máu. ");
        } else {
            description.append(String.format("Tính đến nay đã hiến máu %d lần, tổng cộng %d ml. ", donationCount, totalVolume));
        }

        // Lý do cấp bằng khen (nằm trong mô tả)
        if (certificateType == CertificateType.MERIT && meritReasonDescription != null) {
            description.append(meritReasonDescription);
        }

        description.append("Xin trân trọng cảm ơn nghĩa cử cao đẹp của bạn.");

        // Tạo certificate
        Certificate certificate = new Certificate();
        certificate.setUser(user);
        certificate.setCertificateType(certificateType);
        certificate.setCreateAt(LocalDateTime.now());
        certificate.setUpdateAt(LocalDateTime.now());
        certificate.setDescription(description.toString().trim());

        Certificate saved = certificateRepository.save(certificate);
        return mapToResponse(saved);
    }



    @Override
    public ResponseEntity<?> updateCertificate(Integer certificateId, String newDescription, CertificateType type) {
        try {
            Optional<Certificate> certOpt = certificateRepository.findById(certificateId);
            if (certOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        ResponseObject.builder().status(HttpStatus.NOT_FOUND).message("Không tìm thấy chứng chỉ").build()
                );
            }

            Certificate cert = certOpt.get();
            cert.setDescription(newDescription);
            cert.setCertificateType(type);
            Certificate updated = certificateRepository.save(cert);

            return ResponseEntity.ok(
                    ResponseObject.builder().status(HttpStatus.OK).message("Cập nhật chứng chỉ thành công").data(mapToResponse(updated)).build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ResponseObject.builder().status(HttpStatus.INTERNAL_SERVER_ERROR).message("Lỗi: " + e.getMessage()).build()
            );
        }
    }

    @Override
    public ResponseEntity<?> deleteCertificate(Integer certificateId) {
        try {
            if (!certificateRepository.existsById(certificateId)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        ResponseObject.builder().status(HttpStatus.NOT_FOUND).message("Không tìm thấy chứng chỉ").build()
                );
            }
            certificateRepository.deleteById(certificateId);
            return ResponseEntity.ok(
                    ResponseObject.builder().status(HttpStatus.OK).message("Xóa chứng chỉ thành công").build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ResponseObject.builder().status(HttpStatus.INTERNAL_SERVER_ERROR).message("Lỗi: " + e.getMessage()).build()
            );
        }
    }

    @Override
    public ResponseEntity<?> getCertificatesByUserId(Integer userId, int page, int size) {
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
            Page<Certificate> certificatesPage = certificateRepository.findByUser(user, pageable);
            
            List<CertificateResponse> responseList = certificatesPage.getContent().stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("content", responseList);
            response.put("totalItems", certificatesPage.getTotalElements());
            response.put("totalPages", certificatesPage.getTotalPages());
            response.put("currentPage", page);

            return ResponseEntity.ok(
                    ResponseObject.builder()
                            .status(HttpStatus.OK)
                            .message("Truy xuất chứng chỉ thành công")
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
    public ResponseEntity<?> getCertificateById(Integer certificateId) {
        try {
            Optional<Certificate> certificateOpt = certificateRepository.findById(certificateId);
            return certificateOpt.map(certificate -> ResponseEntity.ok(
                    ResponseObject.builder()
                            .status(HttpStatus.OK)
                            .message("Truy xuất chứng chỉ thành công")
                            .data(mapToResponse(certificate))
                            .build()
            )).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ResponseObject.builder()
                            .status(HttpStatus.NOT_FOUND)
                            .message("Không tìm thấy chứng chỉ")
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
    public ResponseEntity<?> getAllCertificates(int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("createAt").descending());
            Page<Certificate> certs = certificateRepository.findAll(pageable);

            List<CertificateResponse> list = certs.getContent().stream().map(this::mapToResponse).collect(Collectors.toList());
            Map<String, Object> response = new HashMap<>();
            response.put("content", list);
            response.put("totalItems", certs.getTotalElements());
            response.put("totalPages", certs.getTotalPages());
            response.put("currentPage", page);

            return ResponseEntity.ok(ResponseObject.builder().status(HttpStatus.OK).message("Truy xuất tất cả chứng chỉ thành công").data(response).build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseObject.builder().status(HttpStatus.INTERNAL_SERVER_ERROR).message("Lỗi: " + e.getMessage()).build());
        }
    }


    private CertificateResponse mapToResponse(Certificate certificate) {
        return CertificateResponse.builder()
                .certificateId(certificate.getCertificateId())
                .userId(certificate.getUser().getId())
                .userName(certificate.getUser().getFullName())
                .certificateType(certificate.getCertificateType())
                .description(certificate.getDescription())
                .createAt(certificate.getCreateAt() != null ? certificate.getCreateAt() : null)
                .updateAt(certificate.getUpdateAt() != null ? certificate.getUpdateAt() : null)
                .build();
    }

}