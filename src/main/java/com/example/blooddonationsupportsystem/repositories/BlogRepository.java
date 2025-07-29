package com.example.blooddonationsupportsystem.repositories;

import com.example.blooddonationsupportsystem.models.Blog;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlogRepository extends JpaRepository<Blog, Integer> {
    Page<Blog> findByAuthorId(Integer authorId, Pageable pageable);

    Page<Blog> findAll(Pageable pageable);
}
