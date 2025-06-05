package com.example.blooddonationsupportsystem.dtos.responses.reminder;

import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ReminderResponse {
    private Integer reminderId;

    private Integer userId;

    private LocalDate nextDate;

    private String reminderType;

    private String message;

    private Boolean sent = false;

}
