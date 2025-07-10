package com.example.blooddonationsupportsystem.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "blood_components")
@Data//toString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class BloodComponent {
    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer componentId;

    @Column(nullable = false, unique = true)
    private String componentName;

    @ManyToMany(mappedBy = "components")
    private Set<BloodType> bloodTypes = new HashSet<>();
}
