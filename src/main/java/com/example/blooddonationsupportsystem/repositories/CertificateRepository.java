package com.example.blooddonationsupportsystem.repositories;

import com.example.blooddonationsupportsystem.models.Certificate;
import com.example.blooddonationsupportsystem.models.User;
import com.example.blooddonationsupportsystem.utils.CertificateType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, Integer> {
    Page<Certificate> findByUser(User user, Pageable pageable);
    List<Certificate> findByUserAndCertificateType(User user, CertificateType certificateType);
}