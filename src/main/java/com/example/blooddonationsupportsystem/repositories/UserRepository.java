package com.example.blooddonationsupportsystem.repositories;

import com.example.blooddonationsupportsystem.models.User;
import com.example.blooddonationsupportsystem.utils.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    @Query("SELECT u FROM User u where u.email = :email AND u.status = true ")
    Optional<User> findByEmail(String email);

    boolean existsByEmail(@Email(message = "Invalid email format") @NotBlank(message = "Email is required") String email);

    Optional<User> findUserByEmail(String userEmail);

    @Query("SELECT u FROM User u WHERE u.status = true AND " +
            "(6371 * acos(cos(radians(:lat)) * cos(radians(u.latitude)) * cos(radians(u.longitude) - radians(:lon)) + sin(radians(:lat)) * sin(radians(u.latitude)))) <= :radiusKm")
    Page<User> findUsersNearby(@Param("lat") Double lat,
                               @Param("lon") Double lon,
                               @Param("radiusKm") Double radiusKm,
                               Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.bloodType.bloodTypeId = :bloodTypeId")
    List<User> findAllByBloodTypeId(@Param("bloodTypeId") Integer bloodTypeId);

    List<User> findAllByRoleAndBloodType_BloodTypeId(Role role, Integer bloodType_bloodTypeId);
}
