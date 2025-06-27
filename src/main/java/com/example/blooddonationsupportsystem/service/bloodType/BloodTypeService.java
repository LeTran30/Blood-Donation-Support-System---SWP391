package com.example.blooddonationsupportsystem.service.bloodType;

import com.example.blooddonationsupportsystem.dtos.request.bloodType.BloodTypeRequest;
import com.example.blooddonationsupportsystem.dtos.responses.bloodComponent.BloodComponentResponse;
import com.example.blooddonationsupportsystem.dtos.responses.bloodType.BloodTypeResponse;
import com.example.blooddonationsupportsystem.models.BloodComponent;
import com.example.blooddonationsupportsystem.models.BloodType;
import com.example.blooddonationsupportsystem.repositories.BloodComponentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.blooddonationsupportsystem.repositories.BloodTypeRepository;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BloodTypeService implements IBloodTypeService {
    private final BloodComponentRepository bloodComponentRepository;
    private final BloodTypeRepository bloodTypeRepository;

    @Override
    public Page<BloodTypeResponse> getAllBloodTypes(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<BloodType> bloodPage = bloodTypeRepository.findAll(pageable);

        List<BloodTypeResponse> responseList = bloodPage.getContent().stream()
                .map(this::mapToResponse)
                .toList();

        return new PageImpl<>(responseList, pageable, bloodPage.getTotalElements());
    }

    @Override
    public BloodTypeResponse createBloodType(BloodTypeRequest request) {
        BloodType bloodType = new BloodType();
        bloodType.setTypeName(request.getTypeName());

        if (request.getComponentIds() != null && !request.getComponentIds().isEmpty()) {
            List<BloodComponent> components = bloodComponentRepository.findAllById(request.getComponentIds());
            bloodType.setComponents(new HashSet<>(components));
        }

        bloodType = bloodTypeRepository.save(bloodType);

        return mapToResponse(bloodType);
    }


    @Override
    public BloodTypeResponse updateBloodType(Integer id, BloodTypeRequest request) {
        BloodType bloodType = bloodTypeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("BloodType not found"));

        bloodType.setTypeName(request.getTypeName());

        if (request.getComponentIds() != null) {
            List<BloodComponent> components = bloodComponentRepository.findAllById(request.getComponentIds());
            bloodType.setComponents(new HashSet<>(components));
        }

        bloodType = bloodTypeRepository.save(bloodType);

        return mapToResponse(bloodType);
    }


    @Override
    public void deleteBloodType(Integer id) {
        if (!bloodTypeRepository.existsById(id)) {
            throw new EntityNotFoundException("BloodType not found");
        }
        bloodTypeRepository.deleteById(id);
    }

    private BloodTypeResponse mapToResponse(BloodType bloodType) {
        Set<BloodComponentResponse> componentResponses = bloodType.getComponents().stream()
                .map(component -> BloodComponentResponse.builder()
                        .componentId(component.getComponentId())
                        .componentName(component.getComponentName())
                        .build())
                .collect(Collectors.toSet());

        return BloodTypeResponse.builder()
                .id(bloodType.getBloodTypeId())
                .typeName(bloodType.getTypeName())
                .components(componentResponses)
                .build();
    }

}
