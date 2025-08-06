package com.example.blooddonationsupportsystem.repositories;

import com.example.blooddonationsupportsystem.models.Appointment;
import com.example.blooddonationsupportsystem.models.BloodDonationInformation;
import com.example.blooddonationsupportsystem.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BloodDonationInformationRepository extends JpaRepository<BloodDonationInformation, Integer> {
    Optional<BloodDonationInformation> findByAppointmentAppointmentId(Integer appointmentId);
    
    @Query("SELECT bdi FROM BloodDonationInformation bdi WHERE bdi.appointment.user = ?1 ORDER BY bdi.createAt DESC")
    List<BloodDonationInformation> findByUserOrderByCreateAtDesc(User user);
    
    @Query("SELECT SUM(bdi.actualBloodVolume) FROM BloodDonationInformation bdi WHERE bdi.appointment.user = ?1")
    Integer getTotalBloodVolumeByUser(User user);

    boolean existsByAppointment(Appointment appointment);

    @Query("SELECT MIN(b.createAt) FROM BloodDonationInformation b WHERE b.appointment.user = :user")
    Optional<LocalDate> findEarliestDonationDateByUser(@Param("user") User user);

    long countByAppointment_User(User user);
}