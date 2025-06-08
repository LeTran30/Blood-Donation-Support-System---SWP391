package com.example.blooddonationsupportsystem.controller;

import com.example.blooddonationsupportsystem.dtos.request.reminder.ReminderRequest;
import com.example.blooddonationsupportsystem.dtos.responses.ResponseObject;
import com.example.blooddonationsupportsystem.models.User;
import com.example.blooddonationsupportsystem.repositories.UserRepository;
import com.example.blooddonationsupportsystem.service.reminder.IReminderService;
import com.example.blooddonationsupportsystem.utils.ReminderType;
import com.example.blooddonationsupportsystem.utils.Role;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reminder")
@RequiredArgsConstructor
@CrossOrigin("*")
public class ReminderController {

    private final IReminderService reminderService;
    private final UserRepository userRepository;

    @PostMapping
    @PreAuthorize("true")
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
        return reminderService.sendReminder(request);
    }

    @GetMapping("/{reminderId}")
    @PreAuthorize("hasAnyAuthority('member:read', 'staff:read')")
    public ResponseEntity<?> getReminderById(@PathVariable Integer reminderId) {
        var response = reminderService.getReminderById(reminderId);
        if (response.getStatusCode().is4xxClientError()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseObject.builder()
                            .status(HttpStatus.NOT_FOUND)
                            .message("Reminder with id " + reminderId + " not found")
                            .build());
        }
        return response;
    }

    @PutMapping("/{reminderId}")
    @PreAuthorize("hasAnyAuthority('member:update', 'staff:update')")
    public ResponseEntity<?> updateReminder(
            @PathVariable Integer reminderId,
            @Valid @RequestBody ReminderRequest request,
            BindingResult result) {
        if (result.hasErrors()) {
            List<String> errors = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest()
                    .body(ResponseObject.builder()
                            .status(HttpStatus.BAD_REQUEST)
                            .message(errors.toString())
                            .build());
        }
        var response = reminderService.updateReminder(reminderId, request);
        if (response.getStatusCode().is4xxClientError()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseObject.builder()
                            .status(HttpStatus.NOT_FOUND)
                            .message("Reminder with id " + reminderId + " not found")
                            .build());
        }
        return response;
    }


    @DeleteMapping("/{reminderId}")
    @PreAuthorize("hasAnyAuthority('member:delete', 'staff:delete')")
    public ResponseEntity<?> deleteReminder(@PathVariable Integer reminderId) {
        var response = reminderService.deleteReminder(reminderId);
        if (response.getStatusCode().is4xxClientError()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseObject.builder()
                            .status(HttpStatus.NOT_FOUND)
                            .message("Reminder with id " + reminderId + " not found")
                            .build());
        }
        return response;
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('member:read', 'staff:read')")
    public ResponseEntity<?> getRemindersWithFilter(
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) Boolean sent,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(required = false) ReminderType reminderType
    ) {
        return reminderService.getRemindersWithFilter(userId, sent, fromDate, toDate, reminderType);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyAuthority('member:read', 'staff:read')")
    public ResponseEntity<?> getUserReminders(
            @PathVariable Integer userId,
            Principal principal
    ) {
        User currentUser = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (currentUser.getRole() == Role.MEMBER && !currentUser.getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ResponseObject.builder()
                            .status(HttpStatus.FORBIDDEN)
                            .message("You do not have permission to access this user")
                            .build());
        }

        return reminderService.getUserReminders(userId);
    }

}
