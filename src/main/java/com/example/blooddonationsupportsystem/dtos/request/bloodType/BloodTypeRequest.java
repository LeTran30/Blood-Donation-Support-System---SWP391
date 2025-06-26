package com.example.blooddonationsupportsystem.dtos.request.bloodType;

import com.example.blooddonationsupportsystem.utils.BloodTypeName;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BloodTypeRequest {
    private Integer bloodTypeId;
    @NotEmpty
    private BloodTypeName typeName;
}
