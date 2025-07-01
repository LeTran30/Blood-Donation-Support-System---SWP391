package com.example.blooddonationsupportsystem.dtos.request.bloodComponent;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BloodComponentRequest {
    @NotNull(message = "Component name must not be null")
    private String componentName;

}
