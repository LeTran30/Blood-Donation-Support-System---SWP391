package com.example.blooddonationsupportsystem.models;

import com.example.blooddonationsupportsystem.utils.ReminderType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "reminders")
@Data//toString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Reminder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer reminderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference(value = "user-reminder")
    private User user;

    @Column(nullable = false)
    private LocalDate nextDate;

    @Enumerated(EnumType.STRING)
    private ReminderType reminderType;

    private String message;

    private Boolean sent = false;
}
