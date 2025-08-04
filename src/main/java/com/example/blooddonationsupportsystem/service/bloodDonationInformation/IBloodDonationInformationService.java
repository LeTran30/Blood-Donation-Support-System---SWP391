package com.example.blooddonationsupportsystem.service.bloodDonationInformation;

import com.example.blooddonationsupportsystem.dtos.request.bloodDonationInfo.BloodDonationInformationRequest;
import org.springframework.http.ResponseEntity;

public interface IBloodDonationInformationService {
    ResponseEntity<?> createBloodDonationInformation(BloodDonationInformationRequest request);
    ResponseEntity<?> getBloodDonationInformationByAppointmentId(Integer appointmentId);
    ResponseEntity<?> getBloodDonationInformationByUserId(Integer userId, int page, int size);
    ResponseEntity<?> getTotalBloodVolumeByUser(Integer userId);
    ResponseEntity<?> updateBloodDonationInformation(Integer id, BloodDonationInformationRequest request);
    ResponseEntity<?> deleteBloodDonationInformation(Integer id);
    ResponseEntity<?> getAllBloodDonationInformation(int page, int size);

}