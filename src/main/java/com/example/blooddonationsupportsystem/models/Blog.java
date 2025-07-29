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
    private Integer id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT") 
    private String content;

    @ManyToOne(fetch = FetchType.LAZY) // hoặc EAGER tùy nhu cầu
    @JoinColumn(name = "author_id", nullable = false)
    private User author;
}
