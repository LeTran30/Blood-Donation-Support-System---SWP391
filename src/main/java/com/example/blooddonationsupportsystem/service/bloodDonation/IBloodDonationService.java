package com.example.blooddonationsupportsystem.service.bloodDonation;

import com.example.blooddonationsupportsystem.dtos.request.bloodDonation.BloodDonationRequest;
import com.example.blooddonationsupportsystem.dtos.responses.bloodDonation.BloodDonationResponse;
import com.example.blooddonationsupportsystem.models.BloodDonation;
import com.example.blooddonationsupportsystem.utils.DonationStatus;
import org.springframework.data.domain.Page;


public interface IBloodDonationService {
    BloodDonationResponse getBloodDonationById(int id);
    Page<BloodDonationResponse> getAllBloodDonations(int page, int size);
    BloodDonation addBloodDonation(BloodDonationRequest bloodDonationRequest);
    BloodDonation updateDonationStatus(int donationId, DonationStatus newStatus);
}
