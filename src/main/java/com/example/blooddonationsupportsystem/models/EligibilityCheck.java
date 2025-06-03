package com.example.blooddonationsupportsystem.models;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "eligibility_checks")
@Data//toString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EligibilityCheck extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long checkId;

    @ManyToOne
    private User user;

    private LocalDate checkDate;
    private Boolean isEligible;
    private String reason;
}
