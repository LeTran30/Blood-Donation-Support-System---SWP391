package com.example.blooddonationsupportsystem.dtos.responses.bloodType;

import com.example.blooddonationsupportsystem.dtos.responses.bloodComponent.BloodComponentResponse;
import jakarta.persistence.Column;
import lombok.*;

import java.util.Set;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BloodTypeResponse {
    private Integer id;
    private String typeName;
    private Set<BloodComponentResponse> components;
    private String canDonateTo;
    private String canReceiveFrom;
}
