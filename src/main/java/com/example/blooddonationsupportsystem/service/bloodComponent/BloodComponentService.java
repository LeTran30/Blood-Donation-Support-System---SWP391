package com.example.blooddonationsupportsystem.service.bloodComponent;

import com.example.blooddonationsupportsystem.dtos.request.bloodComponent.BloodComponentRequest;
import com.example.blooddonationsupportsystem.dtos.responses.bloodComponent.BloodComponentResponse;
import com.example.blooddonationsupportsystem.models.BloodComponent;
import com.example.blooddonationsupportsystem.repositories.BloodComponentRepository;
import jakarta.persistence.EntityNotFoundException;
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

    @Override
    public BloodComponentResponse createBloodComponent(BloodComponentRequest request) {
        BloodComponent component = new BloodComponent();
        component.setComponentName(request.getComponentName());
        bloodComponentRepository.save(component);
        return BloodComponentResponse.builder()
                .componentId(component.getComponentId())
                .componentName(component.getComponentName())
                .build();
    }

    @Override
    public BloodComponentResponse updateBloodComponent(Integer id, BloodComponentRequest request) {
        BloodComponent existing = bloodComponentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy thành phần máu"));

        existing.setComponentName(request.getComponentName());
        bloodComponentRepository.save(existing);
        return BloodComponentResponse.builder()
                .componentId(existing.getComponentId())
                .componentName(existing.getComponentName())
                .build();
    }

    @Override
    public void deleteBloodComponent(Integer id) {
        if (!bloodComponentRepository.existsById(id)) {
            throw new EntityNotFoundException("Không tìm thấy thành phần máu");
        }
        bloodComponentRepository.deleteById(id);
    }
}
