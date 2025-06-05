package com.example.blooddonationsupportsystem.dtos.responses.bloodType;

import com.example.blooddonationsupportsystem.utils.BloodTypeName;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BloodTypeResponse {
    private BloodTypeName typeName;
}
