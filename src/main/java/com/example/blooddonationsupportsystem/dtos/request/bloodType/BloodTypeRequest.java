package com.example.blooddonationsupportsystem.dtos.request.bloodType;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BloodTypeRequest {
    @NotEmpty
    private String typeName;
    private String canDonateTo;
    private String canReceiveFrom;
    private Set<Integer> componentIds; // List of IDs of BloodComponent
}
