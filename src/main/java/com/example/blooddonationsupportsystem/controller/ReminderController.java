package com.example.blooddonationsupportsystem.controller;

import com.example.blooddonationsupportsystem.dtos.request.reminder.ReminderRequest;
import com.example.blooddonationsupportsystem.dtos.responses.ResponseObject;
import com.example.blooddonationsupportsystem.service.reminder.IReminderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reminder")
@RequiredArgsConstructor
@CrossOrigin("*")
public class ReminderController {

    private final IReminderService reminderService;

    @GetMapping("/user/{userId}")
    @PreAuthorize("true")
    public ResponseEntity<?> getUserReminders(@PathVariable Integer userId) {
        return reminderService.getUserReminders(userId);
    }

    @PostMapping
    @PreAuthorize("true")
    public ResponseEntity<?> createReminder(@RequestBody ReminderRequest request) {
        return reminderService.sendReminder(request);
    }

    @GetMapping("/{reminderId}")
    @PreAuthorize("true")
    public ResponseEntity<?> getReminderById(@PathVariable Integer reminderId) {
        return reminderService.getReminderById(reminderId);
    }

    @PutMapping("/{reminderId}")
    @PreAuthorize("true")
    public ResponseEntity<?> updateReminder(
            @PathVariable Integer reminderId,
            @RequestBody ReminderRequest request) {
        return reminderService.updateReminder(reminderId, request);
    }

    @DeleteMapping("/{reminderId}")
    @PreAuthorize("true")
    public ResponseEntity<?> deleteReminder(@PathVariable Integer reminderId) {
        return reminderService.deleteReminder(reminderId);
    }

    @GetMapping
    @PreAuthorize("true")
    public ResponseEntity<?> getRemindersWithFilter(
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) Boolean sent,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        return reminderService.getRemindersWithFilter(userId, sent, fromDate, toDate);
    }
}
