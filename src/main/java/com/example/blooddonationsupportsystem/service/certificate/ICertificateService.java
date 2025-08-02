package com.example.blooddonationsupportsystem.service.certificate;

import com.example.blooddonationsupportsystem.utils.CertificateType;
import org.springframework.http.ResponseEntity;

public interface ICertificateService {
    ResponseEntity<?> generateCertificateForDonation(Integer bloodDonationInforId);

    ResponseEntity<?> updateCertificate(Integer certificateId, String newDescription, CertificateType type);

    ResponseEntity<?> deleteCertificate(Integer certificateId);

    ResponseEntity<?> getCertificatesByUserId(Integer userId, int page, int size);
    ResponseEntity<?> getCertificateById(Integer certificateId);

    ResponseEntity<?> getAllCertificates(int page, int size);
}