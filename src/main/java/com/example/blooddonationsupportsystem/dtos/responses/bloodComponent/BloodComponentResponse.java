package com.example.blooddonationsupportsystem.dtos.responses.bloodComponent;

import com.example.blooddonationsupportsystem.utils.BloodComponentName;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BloodComponentResponse {
    private Integer componentId;
    private BloodComponentName componentName;
}
