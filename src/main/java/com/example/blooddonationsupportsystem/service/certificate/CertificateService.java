package com.example.blooddonationsupportsystem.service.certificate;

import com.example.blooddonationsupportsystem.dtos.responses.ResponseObject;
import com.example.blooddonationsupportsystem.dtos.responses.certificate.CertificateResponse;
import com.example.blooddonationsupportsystem.models.BloodDonationInformation;
import com.example.blooddonationsupportsystem.models.Certificate;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CertificateService implements ICertificateService {
    private final CertificateRepository certificateRepository;
    private final BloodDonationInformationRepository bloodDonationInformationRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ResponseEntity<?> generateCertificateForDonation(Integer bloodDonationInforId) {
        try {
            // Find blood donation information
            Optional<BloodDonationInformation> bloodDonationInforOpt = bloodDonationInformationRepository.findById(bloodDonationInforId);
            if (bloodDonationInforOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(
                        ResponseObject.builder()
                                .status(HttpStatus.BAD_REQUEST)
                                .message("Blood donation information not found")
                                .build()
                );
            }

            BloodDonationInformation bloodDonationInformation = bloodDonationInforOpt.get();
            User user = bloodDonationInformation.getAppointment().getUser();
            
            // Get total blood volume donated by user
            Integer totalVolume = bloodDonationInformationRepository.getTotalBloodVolumeByUser(user);
            if (totalVolume == null) {
                totalVolume = bloodDonationInformation.getActualBloodVolume();
            }
            
            // Check if this is the first donation
            List<BloodDonationInformation> previousDonations = bloodDonationInformationRepository.findByUserOrderByCreateAtDesc(user);
            boolean isFirstDonation = previousDonations.size() <= 1; // Including the current one
            
            // Generate certificate
            Certificate certificate = Certificate.builder()
                    .user(user)
                    .certificateType(CertificateType.CERTIFICATE) // Default type
                    .build();
            certificate.setCreateAt(LocalDateTime.now());
            // Generate description based on donation details
            String donationDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            String description = String.format(
                    "This certificate is awarded to %s for donating %d ml of blood on %s.",
                    user.getFullName(),
                    bloodDonationInformation.getActualBloodVolume(),
                    donationDate
            );
            
            // Check if user qualifies for a merit certificate
            if (totalVolume >= 1000 || (isFirstDonation && bloodDonationInformation.getActualBloodVolume() >= 350)) {
                certificate.setCertificateType(CertificateType.MERIT);
                description += " This merit certificate recognizes your outstanding contribution to saving lives through blood donation.";
            }
            
            certificate.setDescription(description);
            
            Certificate saved = certificateRepository.save(certificate);
            
            return ResponseEntity.ok(
                    ResponseObject.builder()
                            .status(HttpStatus.CREATED)
                            .message("Certificate generated successfully")
                            .data(mapToResponse(saved))
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ResponseObject.builder()
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .message("Error: " + e.getMessage())
                            .build()
            );
        }
    }

    @Override
    public ResponseEntity<?> updateCertificate(Integer certificateId, String newDescription, CertificateType type) {
        try {
            Optional<Certificate> certOpt = certificateRepository.findById(certificateId);
            if (certOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        ResponseObject.builder().status(HttpStatus.NOT_FOUND).message("Certificate not found").build()
                );
            }

            Certificate cert = certOpt.get();
            cert.setDescription(newDescription);
            cert.setCertificateType(type);
            Certificate updated = certificateRepository.save(cert);

            return ResponseEntity.ok(
                    ResponseObject.builder().status(HttpStatus.OK).message("Certificate updated successfully").data(mapToResponse(updated)).build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ResponseObject.builder().status(HttpStatus.INTERNAL_SERVER_ERROR).message("Error: " + e.getMessage()).build()
            );
        }
    }

    @Override
    public ResponseEntity<?> deleteCertificate(Integer certificateId) {
        try {
            if (!certificateRepository.existsById(certificateId)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        ResponseObject.builder().status(HttpStatus.NOT_FOUND).message("Certificate not found").build()
                );
            }
            certificateRepository.deleteById(certificateId);
            return ResponseEntity.ok(
                    ResponseObject.builder().status(HttpStatus.OK).message("Certificate deleted successfully").build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ResponseObject.builder().status(HttpStatus.INTERNAL_SERVER_ERROR).message("Error: " + e.getMessage()).build()
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
                                .message("User not found")
                                .build()
                );
            }

            User user = userOpt.get();
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
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
                            .message("Certificates retrieved successfully")
                            .data(response)
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ResponseObject.builder()
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .message("Error: " + e.getMessage())
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
                            .message("Certificate retrieved successfully")
                            .data(mapToResponse(certificate))
                            .build()
            )).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ResponseObject.builder()
                            .status(HttpStatus.NOT_FOUND)
                            .message("Certificate not found")
                            .build()
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ResponseObject.builder()
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .message("Error: " + e.getMessage())
                            .build()
            );
        }
    }
    @Override
    public ResponseEntity<?> getAllCertificates(int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            Page<Certificate> certs = certificateRepository.findAll(pageable);

            List<CertificateResponse> list = certs.getContent().stream().map(this::mapToResponse).collect(Collectors.toList());
            Map<String, Object> response = new HashMap<>();
            response.put("content", list);
            response.put("totalItems", certs.getTotalElements());
            response.put("totalPages", certs.getTotalPages());
            response.put("currentPage", page);

            return ResponseEntity.ok(ResponseObject.builder().status(HttpStatus.OK).message("All certificates retrieved").data(response).build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseObject.builder().status(HttpStatus.INTERNAL_SERVER_ERROR).message("Error: " + e.getMessage()).build());
        }
    }


    private CertificateResponse mapToResponse(Certificate certificate) {
        return CertificateResponse.builder()
                .certificateId(certificate.getCertificateId())
                .userId(certificate.getUser().getId())
                .userName(certificate.getUser().getFullName())
                .certificateType(certificate.getCertificateType())
                .description(certificate.getDescription())
                .createAt(certificate.getCreateAt())
                .updateAt(certificate.getUpdateAt())
                .build();
    }
}