package com.example.blooddonationsupportsystem.service.bloodCompoonent;

import com.example.blooddonationsupportsystem.dtos.responses.BloodComponent.BloodComponentResponse;
import com.example.blooddonationsupportsystem.models.BloodComponent;
import com.example.blooddonationsupportsystem.repositories.BloodComponentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BloodComponentService implements IBloodComponentService {
    private final BloodComponentRepository bloodComponentRepository;

    @Override
    public List<BloodComponentResponse> getAllBloodComponents() {
        List<BloodComponent> bloodResponse = bloodComponentRepository.findAll();
        return bloodResponse.stream()
                .map(bloodComponent -> BloodComponentResponse.builder()
                        .componentId(bloodComponent.getComponentId())
                        .componentName(bloodComponent.getComponentName())
                        .build())
                .toList();
    }
}
