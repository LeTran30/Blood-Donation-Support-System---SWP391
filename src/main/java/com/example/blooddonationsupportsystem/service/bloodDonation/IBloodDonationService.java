package com.example.blooddonationsupportsystem.service.bloodDonation;

import com.example.blooddonationsupportsystem.dtos.request.bloodDonation.BloodDonationRequest;
import com.example.blooddonationsupportsystem.dtos.responses.bloodDonation.BloodDonationResponse;

import java.util.List;

public interface IBloodDonationService {
    BloodDonationResponse getBloodDonationById(int id);
    List<BloodDonationResponse> getAllBloodDonations();
    void addBloodDonation(BloodDonationRequest bloodDonationRequest);
}
