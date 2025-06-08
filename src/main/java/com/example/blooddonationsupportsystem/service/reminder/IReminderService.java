package com.example.blooddonationsupportsystem.service.reminder;

import com.example.blooddonationsupportsystem.dtos.request.reminder.ReminderRequest;
import com.example.blooddonationsupportsystem.utils.ReminderType;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

public interface IReminderService {
    ResponseEntity<?> getUserReminders(Integer userId);

    void createNextDonationReminder(Integer userId, LocalDate lastDonationDate);

    ResponseEntity<?> sendReminder(ReminderRequest request);

    ResponseEntity<?> getReminderById(Integer reminderId);

    ResponseEntity<?> updateReminder(Integer reminderId, ReminderRequest request);

    ResponseEntity<?> deleteReminder(Integer reminderId);

    ResponseEntity<?> getRemindersWithFilter(Integer userId, Boolean sent, LocalDate fromDate, LocalDate toDate, ReminderType reminderType);
}
