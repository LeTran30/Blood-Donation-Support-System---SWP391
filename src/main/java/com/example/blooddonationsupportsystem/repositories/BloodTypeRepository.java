package com.example.blooddonationsupportsystem.repositories;

import com.example.blooddonationsupportsystem.models.BloodType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface BloodTypeRepository extends JpaRepository<BloodType, Integer> {

    @EntityGraph(attributePaths = "components")
    @Query("SELECT bt FROM BloodType bt")
    Page<BloodType> findAllWithComponents(Pageable pageable);

}
