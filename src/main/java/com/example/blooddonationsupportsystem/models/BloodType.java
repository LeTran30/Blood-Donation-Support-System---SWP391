package com.example.blooddonationsupportsystem.models;

import jakarta.persistence.*;
import lombok.*;

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
    private Long bloodTypeId;
    @Column(name = "type_name")
    private String typeName; // A+, O-, AB+, ...
}
