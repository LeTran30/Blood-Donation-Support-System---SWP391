package com.example.blooddonationsupportsystem.service.bloodDonation;

import com.example.blooddonationsupportsystem.dtos.request.bloodDonation.BloodDonationRequest;
import com.example.blooddonationsupportsystem.dtos.responses.bloodDonation.BloodDonationResponse;
import com.example.blooddonationsupportsystem.models.BloodDonation;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IBloodDonationService {
    BloodDonationResponse getBloodDonationById(int id);
    Page<BloodDonationResponse> getAllBloodDonations(int page, int size);
    BloodDonation addBloodDonation(BloodDonationRequest bloodDonationRequest);
}
