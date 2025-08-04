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
    @NotNull(message = "Cần cung cấp tên thành phần máu")
    private String componentName;

}
