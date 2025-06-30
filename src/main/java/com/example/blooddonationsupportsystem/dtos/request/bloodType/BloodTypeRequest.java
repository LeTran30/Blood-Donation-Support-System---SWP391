package com.example.blooddonationsupportsystem.dtos.request.bloodType;

import com.example.blooddonationsupportsystem.utils.BloodTypeName;
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
    private BloodTypeName typeName;
    private Set<Integer> componentIds; // List of IDs of BloodComponent
}
