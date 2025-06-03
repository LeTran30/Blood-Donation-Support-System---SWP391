package com.example.blooddonationsupportsystem.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "inventories")
@Data//toString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long inventoryId;

    @ManyToOne
    private BloodType bloodType;

    private String componentType; // Hồng cầu, Huyết tương...
    private int quantity;
    private LocalDate lastUpdated;
}
