package com.example.blooddonationsupportsystem.controller;

import com.example.blooddonationsupportsystem.dtos.request.reminder.ReminderRequest;
import com.example.blooddonationsupportsystem.dtos.responses.ResponseObject;
import com.example.blooddonationsupportsystem.service.reminder.IReminderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reminder")
@RequiredArgsConstructor
@CrossOrigin("*")
public class ReminderController {
    private final IReminderService reminderService;

    @PostMapping
    public ResponseEntity<?> createReminder(
            @Valid @RequestBody ReminderRequest request,
            BindingResult result
    ) {
        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(
                    ResponseObject.builder()
                            .status(HttpStatus.BAD_REQUEST)
                            .message(String.valueOf(errorMessages))
                            .build()
            );
        }
        return reminderService.createReminder(request);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserReminders(@PathVariable Integer userId) {
        return reminderService.getUserReminders(userId);
    }
}
