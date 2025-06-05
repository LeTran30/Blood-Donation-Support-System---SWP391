package com.example.blooddonationsupportsystem.service.bloodDonation;

import com.example.blooddonationsupportsystem.dtos.request.bloodDonation.BloodDonationRequest;
import com.example.blooddonationsupportsystem.dtos.responses.bloodDonation.BloodDonationResponse;
import com.example.blooddonationsupportsystem.dtos.responses.user.UserResponse;
import com.example.blooddonationsupportsystem.models.BloodDonation;
import com.example.blooddonationsupportsystem.models.BloodType;
import com.example.blooddonationsupportsystem.models.HealthCheck;
import com.example.blooddonationsupportsystem.models.User;
import com.example.blooddonationsupportsystem.repositories.BloodDonationRepository;
import com.example.blooddonationsupportsystem.repositories.BloodTypeRepository;
import com.example.blooddonationsupportsystem.repositories.HealthCheckRepository;
import com.example.blooddonationsupportsystem.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BloodDonationService implements IBloodDonationService {

    private final BloodDonationRepository bloodDonationRepository;
    private final BloodTypeRepository bloodTypeRepository;
    private final HealthCheckRepository healthCheckRepository;
    private final UserRepository userRepository;
//    private final BloodDonationRequest request;

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
                .healthCheck(donation.getHealthCheck().getHealthCheckId())
                .build();


    }

    @Override
    public List<BloodDonationResponse> getAllBloodDonations() {
        return bloodDonationRepository.findAll().stream()
                .map(donation -> BloodDonationResponse.builder()
                        .donationId(donation.getDonationId())
                        .user(donation.getUser().getId())
                        .bloodType(donation.getBloodType().getBloodTypeId())
                        .donationDate(donation.getDonationDate())
                        .volumeMl(donation.getVolumeMl())
                        .status(donation.getStatus())
                        .healthCheck(donation.getHealthCheck().getHealthCheckId())
                        .build())
                .toList();
    }

    @Override
    public void addBloodDonation(BloodDonationRequest bloodDonationRequest) {
        User user = userRepository.findById(bloodDonationRequest.getUser())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        BloodType bloodType = bloodTypeRepository.findById(bloodDonationRequest.getBloodType())
                .orElseThrow(() -> new IllegalArgumentException("Blood type not found"));

        HealthCheck healthCheck = healthCheckRepository.findById(bloodDonationRequest.getHealthCheck())
                .orElseThrow(() -> new IllegalArgumentException("Health check not found"));

        BloodDonation bloodDonation = BloodDonation.builder()
                .user(user)
                .bloodType(bloodType)
                .donationDate(bloodDonationRequest.getDonationDate())
                .volumeMl(bloodDonationRequest.getVolumeMl())
                .status(bloodDonationRequest.getStatus())
                .healthCheck(healthCheck)
                .build();
        bloodDonationRepository.save(bloodDonation);
    }
}
