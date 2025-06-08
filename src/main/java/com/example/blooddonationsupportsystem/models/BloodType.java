package com.example.blooddonationsupportsystem.models;

import com.example.blooddonationsupportsystem.utils.BloodTypeName;
import jakarta.persistence.*;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "blood_types")
@Data//toString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BloodType extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer bloodTypeId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private BloodTypeName typeName;
}
