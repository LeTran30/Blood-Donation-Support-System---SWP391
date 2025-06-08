package com.example.blooddonationsupportsystem.models;

import com.example.blooddonationsupportsystem.utils.BloodComponentName;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "blood_components")
@Data//toString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BloodComponent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer componentId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private BloodComponentName componentName;
}
