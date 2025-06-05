package com.example.blooddonationsupportsystem.service.reminder;

import com.example.blooddonationsupportsystem.dtos.request.reminder.ReminderRequest;
import com.example.blooddonationsupportsystem.dtos.responses.reminder.ReminderResponse;
import com.example.blooddonationsupportsystem.models.Reminder;
import com.example.blooddonationsupportsystem.models.User;
import com.example.blooddonationsupportsystem.repositories.ReminderRepository;
import com.example.blooddonationsupportsystem.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReminderFactory {
    private final ReminderRepository reminderRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    public ReminderResponse createReminder(ReminderRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Reminder reminder = Reminder.builder()
                .reminderType(request.getReminderType())
                .nextDate(request.getNextDate())
                .message(request.getMessage())
                .sent(Boolean.TRUE.equals(request.getSent()))
                .user(user)
                .build();

        Reminder savedReminder = reminderRepository.save(reminder);

        ReminderResponse response = modelMapper.map(savedReminder, ReminderResponse.class);
        response.setUserId(user.getId());
        return response;
    }
}