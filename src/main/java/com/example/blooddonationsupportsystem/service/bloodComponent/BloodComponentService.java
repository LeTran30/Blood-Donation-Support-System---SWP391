package com.example.blooddonationsupportsystem.service.bloodComponent;

import com.example.blooddonationsupportsystem.dtos.responses.bloodComponent.BloodComponentResponse;
import com.example.blooddonationsupportsystem.models.BloodComponent;
import com.example.blooddonationsupportsystem.repositories.BloodComponentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BloodComponentService implements IBloodComponentService {
    private final BloodComponentRepository bloodComponentRepository;

    @Override
    public Page<BloodComponentResponse> getAllBloodComponents(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<BloodComponent> bloodPage = bloodComponentRepository.findAll(pageable);

        List<BloodComponentResponse> responseList = bloodPage.getContent().stream()
                .map(bloodComponent -> BloodComponentResponse.builder()
                        .componentId(bloodComponent.getComponentId())
                        .componentName(bloodComponent.getComponentName())
                        .build())
                .toList();

        return new PageImpl<>(responseList, pageable, bloodPage.getTotalElements());
    }
}
