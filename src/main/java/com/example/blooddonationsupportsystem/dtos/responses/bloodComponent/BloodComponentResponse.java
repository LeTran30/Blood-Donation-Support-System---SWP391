package com.example.blooddonationsupportsystem.dtos.responses.bloodComponent;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BloodComponentResponse {
    private Integer componentId;
    private String componentName;
}
