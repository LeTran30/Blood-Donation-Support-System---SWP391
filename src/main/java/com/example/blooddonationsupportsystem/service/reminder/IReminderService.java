package com.example.blooddonationsupportsystem.service.reminder;

import com.example.blooddonationsupportsystem.dtos.request.reminder.ReminderRequest;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

public interface IReminderService {
    @Transactional
    ResponseEntity<?> createReminder(ReminderRequest request);

    ResponseEntity<?> getUserReminders(Integer userId);

    void createNextDonationReminder(Integer userId, LocalDate lastDonationDate);
}
