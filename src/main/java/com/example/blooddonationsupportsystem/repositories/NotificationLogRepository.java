package com.example.blooddonationsupportsystem.repositories;

import com.example.blooddonationsupportsystem.models.NotificationLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationLogRepository extends JpaRepository<NotificationLog, Long> {
}
