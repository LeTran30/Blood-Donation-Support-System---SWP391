package com.example.blooddonationsupportsystem.service.reminder;

import com.example.blooddonationsupportsystem.dtos.request.reminder.ReminderRequest;
import com.example.blooddonationsupportsystem.dtos.responses.ResponseObject;
import com.example.blooddonationsupportsystem.dtos.responses.reminder.ReminderResponse;
import com.example.blooddonationsupportsystem.models.NotificationLog;
import com.example.blooddonationsupportsystem.models.Reminder;
import com.example.blooddonationsupportsystem.models.User;
import com.example.blooddonationsupportsystem.repositories.NotificationLogRepository;
import com.example.blooddonationsupportsystem.repositories.ReminderRepository;
import com.example.blooddonationsupportsystem.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReminderService implements IReminderService {
    private final ReminderRepository reminderRepository;
    private final UserRepository userRepository;
    private final NotificationLogRepository notificationLogRepository;
    private final ModelMapper modelMapper;
    private final IReminderService self;

    @Transactional
    @Override
    public ResponseEntity<?> createReminder(ReminderRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseObject.builder()
                            .status(HttpStatus.NOT_FOUND)
                            .message("User not found")
                            .build());
        }

        Reminder reminder = Reminder.builder()
                .reminderType(request.getReminderType())
                .nextDate(request.getNextDate())
                .message(request.getMessage())
                .user(user)
                .build();

        reminderRepository.save(reminder);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseObject.builder()
                        .status(HttpStatus.CREATED)
                        .message("Reminder created successfully")
                        .data(mapWithUserId(reminder))
                        .build());
    }

    @Override
    public ResponseEntity<?> getUserReminders(Integer userId) {
        if (!userRepository.existsById(userId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseObject.builder()
                            .status(HttpStatus.NOT_FOUND)
                            .message("User not found")
                            .build());
        }

        List<Reminder> reminders = reminderRepository.findAllByUserId(userId);
        return ResponseEntity.ok(
                ResponseObject.builder()
                        .status(HttpStatus.OK)
                        .message("Reminders retrieved successfully")
                        .data(reminders.stream()
                                .map(this::mapWithUserId)
                                .toList())
                        .build()
        );
    }
    @Scheduled(cron = "0 0 8 * * *")
    public void checkReminders() {
        List<Reminder> dueReminders = reminderRepository.findByNextDateAndSentFalse(LocalDate.now());
        for (Reminder reminder : dueReminders) {
//            notificationService.sendReminder(reminder);
            reminder.setSent(true);
            reminderRepository.save(reminder);
        }
    }
    private ReminderResponse mapWithUserId(Reminder reminder) {
        ReminderResponse response = modelMapper.map(reminder, ReminderResponse.class);
        response.setUserId(reminder.getUser().getId());
        return response;
    }
    @Override
    public void createNextDonationReminder(Integer userId, LocalDate lastDonationDate) {
        LocalDate nextDate = lastDonationDate.plusMonths(3);

        ReminderRequest request = ReminderRequest.builder()
                .userId(userId)
                .nextDate(nextDate)
                .reminderType("NEXT_DONATION")
                .message("You're eligible to donate blood again on " + nextDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                .sent(false)
                .build();

        self.createReminder(request);
    }
    private void sendReminder(Reminder reminder) {
        User user = reminder.getUser();
        String message = "[Reminder] " + reminder.getMessage();

        NotificationLog log = NotificationLog.builder()
                .user(user)
                .message(message)
                .build();

        notificationLogRepository.save(log);

    }
}
