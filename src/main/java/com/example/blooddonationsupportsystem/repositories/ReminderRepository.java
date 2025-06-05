package com.example.blooddonationsupportsystem.repositories;

import com.example.blooddonationsupportsystem.models.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReminderRepository extends JpaRepository<Reminder, Integer> {
    List<Reminder> findAllByUserId(Integer userId);

    List<Reminder> findByNextDateAndSentFalse(LocalDate now);
}
