package com.example.blooddonationsupportsystem.models;

import jakarta.persistence.*;
import lombok.*;
@Entity
@Table(name = "blogs")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class Blog extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT") 
    private String content;

    @Column(nullable = false)
    private String author;
}
