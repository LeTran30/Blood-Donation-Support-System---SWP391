package com.example.blooddonationsupportsystem.service.bloodDonation;

import com.example.blooddonationsupportsystem.dtos.request.bloodDonation.BloodDonationRequest;
import com.example.blooddonationsupportsystem.dtos.responses.bloodDonation.BloodDonationResponse;
import com.example.blooddonationsupportsystem.exceptions.EntityNotFoundException;
import com.example.blooddonationsupportsystem.models.*;
import com.example.blooddonationsupportsystem.repositories.*;
import com.example.blooddonationsupportsystem.utils.DonationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BloodDonationService implements IBloodDonationService {

    private final BloodDonationRepository bloodDonationRepository;
    private final BloodTypeRepository bloodTypeRepository;
    private final HealthCheckRepository healthCheckRepository;
    private final UserRepository userRepository;
    private final BloodComponentRepository bloodComponentRepository;

    @Override
    public BloodDonationResponse getBloodDonationById(int id) {
        BloodDonation donation = bloodDonationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy cuộc hiến máu"));
        return BloodDonationResponse.builder()
                .donationId(donation.getDonationId())
                .user(donation.getUser().getId())
                .bloodType(donation.getBloodType().getBloodTypeId())
                .donationDate(donation.getDonationDate())
                .volumeMl(donation.getVolumeMl())
                .status(donation.getStatus())
                .healthCheck(donation.getHealthCheck().getId())
                .build();


    }

    @Override
    public Page<BloodDonationResponse> getAllBloodDonations(int page, int size) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName(); // nếu bạn dùng email làm username trong UserDetails
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy người dùng"));
        Integer currentUserId = user.getId();
        // Lấy danh sách quyền (roles)
        boolean isAdminOrStaff = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN") || auth.getAuthority().equals("ROLE_STAFF"));

        Pageable pageable = PageRequest.of(page, size);
        Page<BloodDonation> bloodPage;

        if (isAdminOrStaff) {
            bloodPage = bloodDonationRepository.findAll(pageable);
        } else {
            bloodPage = bloodDonationRepository.findByUserId(currentUserId, pageable);
        }

        List<BloodDonationResponse> bloodDonationResponses = bloodPage.stream()
                .map(donation -> BloodDonationResponse.builder()
                        .donationId(donation.getDonationId())
                        .user(donation.getUser().getId())
                        .bloodType(donation.getBloodType().getBloodTypeId())
                        .donationDate(donation.getDonationDate())
                        .volumeMl(donation.getVolumeMl())
                        .status(donation.getStatus())
                        .healthCheck(donation.getHealthCheck().getId())
                        .build())
                .toList();

        return new PageImpl<>(bloodDonationResponses, pageable, bloodPage.getTotalElements());
    }


    @Override
    public BloodDonation addBloodDonation(BloodDonationRequest bloodDonationRequest) {
        User user = userRepository.findById(bloodDonationRequest.getUser())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng"));

        BloodType bloodType = bloodTypeRepository.findById(bloodDonationRequest.getBloodType())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy nhóm máu"));

        BloodComponent bloodComponent = bloodComponentRepository.findById(bloodDonationRequest.getBloodComponent())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thành phần máu"));

        HealthCheck healthCheck = healthCheckRepository.findById(bloodDonationRequest.getHealthCheck())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thông kiểm tra sức khỏa"));

        BloodDonation bloodDonation = BloodDonation.builder()
                .user(user)
                .bloodType(bloodType)
                .bloodComponent(bloodComponent)
                .donationDate(bloodDonationRequest.getDonationDate())
                .volumeMl(bloodDonationRequest.getVolumeMl())
                .status(bloodDonationRequest.getStatus())
                .healthCheck(healthCheck)
                .build();
        return bloodDonationRepository.save(bloodDonation);

    }

    @Override
    public BloodDonation updateDonationStatus(int donationId, DonationStatus newStatus) {
        BloodDonation donation = bloodDonationRepository.findById(donationId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy cuộc hiến máu"));

        donation.setStatus(newStatus);
        return bloodDonationRepository.save(donation);
    }
}
