package com.example.blooddonationsupportsystem.models;

import com.example.blooddonationsupportsystem.utils.BloodComponentName;
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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private BloodComponentName componentName;

    @ManyToMany(mappedBy = "components")
    private Set<BloodType> bloodTypes = new HashSet<>();
}
