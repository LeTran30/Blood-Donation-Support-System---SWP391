package com.example.blooddonationsupportsystem.models;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "extraction_details")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExtractionDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "extraction_id", nullable = false)
    private Extraction extraction;

    @ManyToOne
    @JoinColumn(name = "inventory_id", nullable = false)
    private Inventory inventory;

    @Column(nullable = false)
    private Integer volumeExtracted;
}
