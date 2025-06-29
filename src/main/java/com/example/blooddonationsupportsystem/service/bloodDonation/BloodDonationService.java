package com.example.blooddonationsupportsystem.service.bloodDonation;

import com.example.blooddonationsupportsystem.dtos.request.bloodDonation.BloodDonationRequest;
import com.example.blooddonationsupportsystem.dtos.responses.bloodDonation.BloodDonationResponse;
import com.example.blooddonationsupportsystem.models.*;
import com.example.blooddonationsupportsystem.repositories.*;
import com.example.blooddonationsupportsystem.utils.DonationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
                .orElseThrow(() -> new IllegalArgumentException("Blood donation not found"));
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
        Pageable pageable = PageRequest.of(page, size);
        Page<BloodDonation> bloodPage = bloodDonationRepository.findAll(pageable);
        List<BloodDonationResponse> bloodDonationResponses = bloodDonationRepository.findAll().stream()
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
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        BloodType bloodType = bloodTypeRepository.findById(bloodDonationRequest.getBloodType())
                .orElseThrow(() -> new IllegalArgumentException("Blood type not found"));

        BloodComponent bloodComponent = bloodComponentRepository.findById(bloodDonationRequest.getBloodComponent())
                .orElseThrow(() -> new IllegalArgumentException("Blood component not found"));

        HealthCheck healthCheck = healthCheckRepository.findById(bloodDonationRequest.getHealthCheck())
                .orElseThrow(() -> new IllegalArgumentException("Health check not found"));

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
                .orElseThrow(() -> new IllegalArgumentException("Blood donation not found"));

        donation.setStatus(newStatus);
        return bloodDonationRepository.save(donation);
    }
}
