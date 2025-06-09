package com.example.blooddonationsupportsystem.repositories;

import com.example.blooddonationsupportsystem.models.Reminder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReminderRepository extends JpaRepository<Reminder, Integer>,
        JpaSpecificationExecutor<Reminder> {

    Page<Reminder> findAllByUserId(Integer userId, Pageable pageable);

    List<Reminder> findByNextDateAndSentFalse(LocalDate now);
}
