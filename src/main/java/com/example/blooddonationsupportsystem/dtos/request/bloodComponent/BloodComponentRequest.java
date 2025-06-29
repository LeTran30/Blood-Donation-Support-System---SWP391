package com.example.blooddonationsupportsystem.dtos.request.bloodComponent;

import com.example.blooddonationsupportsystem.utils.BloodComponentName;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BloodComponentRequest {
    private Integer componentId;
    @NotNull(message = "Component name must not be null")
    private BloodComponentName componentName;

}
