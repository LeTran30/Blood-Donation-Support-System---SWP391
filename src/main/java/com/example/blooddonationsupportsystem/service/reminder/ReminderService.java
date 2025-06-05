package com.example.blooddonationsupportsystem.service.reminder;

import com.example.blooddonationsupportsystem.dtos.request.reminder.ReminderRequest;
import com.example.blooddonationsupportsystem.dtos.responses.ResponseObject;
import com.example.blooddonationsupportsystem.dtos.responses.reminder.ReminderResponse;
import com.example.blooddonationsupportsystem.models.Appointment;
import com.example.blooddonationsupportsystem.models.NotificationLog;
import com.example.blooddonationsupportsystem.models.Reminder;
import com.example.blooddonationsupportsystem.models.User;
import com.example.blooddonationsupportsystem.repositories.NotificationLogRepository;
import com.example.blooddonationsupportsystem.repositories.ReminderRepository;
import com.example.blooddonationsupportsystem.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReminderService implements IReminderService {
    private final ReminderRepository reminderRepository;
    private final UserRepository userRepository;
    private final NotificationLogRepository notificationLogRepository;
    private final ReminderFactory reminderFactory;
    private final ModelMapper modelMapper;
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
            // TODO: Gọi service gửi notification thực tế ở đây
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

        reminderFactory.createReminder(request);
    }

    @Override
    public ResponseEntity<?> sendReminder(ReminderRequest request) {
        Optional<User> user = userRepository.findById(request.getUserId());
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseObject.builder()
                            .status(HttpStatus.NOT_FOUND)
                            .message("User not found")
                            .build());
        }

        String message = "[Reminder] " + request.getMessage();

        NotificationLog log = NotificationLog.builder()
                .user(user.get())
                .message(message)
                .build();
        notificationLogRepository.save(log);

        ReminderResponse created = reminderFactory.createReminder(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseObject.builder()
                        .status(HttpStatus.CREATED)
                        .message("Reminder created successfully")
                        .data(created)
                        .build());
    }

    @Override
    public ResponseEntity<?> getReminderById(Integer reminderId) {
        Optional<Reminder> reminderOpt = reminderRepository.findById(reminderId);
        if (reminderOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseObject.builder()
                            .status(HttpStatus.NOT_FOUND)
                            .message("Reminder not found")
                            .build());
        }
        ReminderResponse response = mapWithUserId(reminderOpt.get());
        return ResponseEntity.ok(
                ResponseObject.builder()
                        .status(HttpStatus.OK)
                        .message("Reminder retrieved successfully")
                        .data(response)
                        .build()
        );
    }

    @Override
    public ResponseEntity<?> updateReminder(Integer reminderId, ReminderRequest request) {
        Optional<Reminder> reminderOpt = reminderRepository.findById(reminderId);
        if (reminderOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseObject.builder()
                            .status(HttpStatus.NOT_FOUND)
                            .message("Reminder not found")
                            .build());
        }

        Reminder reminder = reminderOpt.get();

        // Update fields nếu có trong request
        if (request.getNextDate() != null) {
            reminder.setNextDate(request.getNextDate());
        }
        if (request.getMessage() != null) {
            reminder.setMessage(request.getMessage());
        }
        if (request.getReminderType() != null) {
            reminder.setReminderType(request.getReminderType());
        }
        if (request.getSent() != null) {
            reminder.setSent(request.getSent());
        }

        reminderRepository.save(reminder);

        ReminderResponse response = mapWithUserId(reminder);
        return ResponseEntity.ok(
                ResponseObject.builder()
                        .status(HttpStatus.OK)
                        .message("Reminder updated successfully")
                        .data(response)
                        .build()
        );
    }
    @Override
    public ResponseEntity<?> deleteReminder(Integer reminderId) {
        if (!reminderRepository.existsById(reminderId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseObject.builder()
                            .status(HttpStatus.NOT_FOUND)
                            .message("Reminder not found")
                            .build());
        }
        reminderRepository.deleteById(reminderId);
        return ResponseEntity.ok(
                ResponseObject.builder()
                        .status(HttpStatus.OK)
                        .message("Reminder deleted successfully")
                        .build()
        );
    }

    @Override
    public ResponseEntity<?> getRemindersWithFilter(Integer userId, Boolean sent, LocalDate fromDate, LocalDate toDate) {
        Specification<Reminder> spec = (root, query, cb) -> cb.conjunction();

        if (userId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("user").get("id"), userId));
        }
        if (sent != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("sent"), sent));
        }
        if (fromDate != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("nextDate"), fromDate));
        }
        if (toDate != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("nextDate"), toDate));
        }

        List<Reminder> reminders = reminderRepository.findAll(spec);
        List<ReminderResponse> responses = reminders.stream()
                .map(this::mapWithUserId)
                .toList();

        return ResponseEntity.ok(
                ResponseObject.builder()
                        .status(HttpStatus.OK)
                        .message("Reminders retrieved successfully")
                        .data(responses)
                        .build()
        );
    }
}
