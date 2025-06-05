package com.example.blooddonationsupportsystem.dtos.request.reminder;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ReminderRequest {
    private Integer reminderId;

    @NotNull(message = "User ID is required")
    private Integer userId;

    @NotNull(message = "Next date is required")
    @FutureOrPresent(message = "Next date must be today or in the future")
    private LocalDate nextDate;

    @NotBlank(message = "Reminder type is required")
    @Size(max = 100, message = "Reminder type must be less than or equal to 100 characters")
    private String reminderType;

    @NotBlank(message = "Message is required")
    @Size(max = 255, message = "Message must be less than or equal to 255 characters")
    private String message;

    private Boolean sent = false;

}
