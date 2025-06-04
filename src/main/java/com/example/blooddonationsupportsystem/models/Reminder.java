package com.example.blooddonationsupportsystem.models;

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

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDate nextDate;

    private String reminderType;
    private String message;

    private Boolean sent = false;
}
