package com.example.blooddonationsupportsystem.repositories;

import com.example.blooddonationsupportsystem.models.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    @Query("SELECT u FROM User u where u.email = :email AND u.status = true ")
    Optional<User> findByEmail(String email);

    boolean existsByEmail(@Email(message = "Invalid email format") @NotBlank(message = "Email is required") String email);

    Optional<User> findUserByEmail(String userEmail);

}
