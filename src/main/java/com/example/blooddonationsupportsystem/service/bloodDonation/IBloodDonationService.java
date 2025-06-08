package com.example.blooddonationsupportsystem.service.bloodDonation;

import com.example.blooddonationsupportsystem.dtos.request.bloodDonation.BloodDonationRequest;
import com.example.blooddonationsupportsystem.dtos.responses.bloodDonation.BloodDonationResponse;
import com.example.blooddonationsupportsystem.models.BloodDonation;

import java.util.List;

public interface IBloodDonationService {
    BloodDonationResponse getBloodDonationById(int id);
    List<BloodDonationResponse> getAllBloodDonations();
    BloodDonation addBloodDonation(BloodDonationRequest bloodDonationRequest);
}
