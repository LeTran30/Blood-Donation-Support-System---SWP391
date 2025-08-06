package com.example.blooddonationsupportsystem.service.bloodType;

import com.example.blooddonationsupportsystem.dtos.request.bloodType.BloodTypeRequest;
import com.example.blooddonationsupportsystem.dtos.responses.bloodComponent.BloodComponentResponse;
import com.example.blooddonationsupportsystem.dtos.responses.bloodType.BloodTypeResponse;
import com.example.blooddonationsupportsystem.models.BloodComponent;
import com.example.blooddonationsupportsystem.models.BloodType;
import com.example.blooddonationsupportsystem.repositories.BloodComponentRepository;
import com.example.blooddonationsupportsystem.repositories.BloodTypeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BloodTypeService implements IBloodTypeService {

    private final BloodComponentRepository bloodComponentRepository;
    private final BloodTypeRepository bloodTypeRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<BloodTypeResponse> getAllBloodTypes(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<BloodType> bloodPage = bloodTypeRepository.findAllWithComponents(pageable);

        List<BloodTypeResponse> responseList = bloodPage.getContent().stream()
                .map(bt -> {
                    Set<BloodComponent> safeCopy = new HashSet<>(bt.getComponents()); // 🔥 Rất quan trọng!
                    return mapToResponse(bt, safeCopy);
                })
                .collect(Collectors.toList());


        return new PageImpl<>(responseList, pageable, bloodPage.getTotalElements());
    }

    @Override
    @Transactional
    public BloodTypeResponse createBloodType(BloodTypeRequest request) {
        BloodType bloodType = BloodType.builder()
                .typeName(request.getTypeName())
                .canDonateTo(request.getCanDonateTo())
                .canReceiveFrom(request.getCanReceiveFrom())
                .build();

        Set<BloodComponent> components = fetchComponentsByIds(request.getComponentIds());
        bloodType.setComponents(components);

        return mapToResponse(bloodTypeRepository.save(bloodType), components);
    }

    @Override
    @Transactional
    public BloodTypeResponse updateBloodType(Integer id, BloodTypeRequest request) {
        BloodType bloodType = bloodTypeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy nhóm máu"));

        bloodType.setTypeName(request.getTypeName());
        bloodType.setCanDonateTo(request.getCanDonateTo());
        bloodType.setCanReceiveFrom(request.getCanReceiveFrom());
        Set<BloodComponent> components = fetchComponentsByIds(request.getComponentIds());
        bloodType.setComponents(components);

        return mapToResponse(bloodTypeRepository.save(bloodType), components);
    }

    @Override
    @Transactional
    public void deleteBloodType(Integer id) {
        BloodType bloodType = bloodTypeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy nhóm máu có ID: " + id));
        bloodTypeRepository.delete(bloodType);
    }

    private BloodTypeResponse mapToResponse(BloodType bloodType, Set<BloodComponent> components) {
        Set<BloodComponentResponse> componentResponses = components.stream()
                .map(c -> BloodComponentResponse.builder()
                        .componentId(c.getComponentId())
                        .componentName(c.getComponentName())
                        .build())
                .collect(Collectors.toSet());

        return BloodTypeResponse.builder()
                .id(bloodType.getBloodTypeId())
                .typeName(bloodType.getTypeName())
                .components(componentResponses)
                .canDonateTo(bloodType.getCanDonateTo())
                .canReceiveFrom(bloodType.getCanReceiveFrom())
                .build();
    }

    private Set<BloodComponent> fetchComponentsByIds(Collection<Integer> ids) {
        if (ids == null || ids.isEmpty()) return new HashSet<>();

        List<BloodComponent> components = bloodComponentRepository.findAllById(ids);
        if (components.isEmpty()) {
            throw new EntityNotFoundException("Không tìm thấy thành phần máu hợp lệ cho ID được cung cấp.");
        }
        return new HashSet<>(components);
    }

    @Transactional
    @Override
    public BloodTypeResponse assignComponentsToBloodType(Integer bloodTypeId, Collection<Integer> componentIds) {
        BloodType bloodType = bloodTypeRepository.findById(bloodTypeId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy nhóm máu có ID: " + bloodTypeId));

        Set<BloodComponent> components = fetchComponentsByIds(componentIds);
        bloodType.setComponents(components);

        return mapToResponse(bloodTypeRepository.save(bloodType), components);
    }

    @Transactional
    @Override
    public BloodTypeResponse removeComponentFromBloodType(Integer bloodTypeId, Integer componentId) {
        BloodType bloodType = bloodTypeRepository.findById(bloodTypeId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy nhóm máu có ID: " + bloodTypeId));

        Set<BloodComponent> components = bloodType.getComponents();
        components.removeIf(c -> c.getComponentId().equals(componentId));
        bloodType.setComponents(components);

        return mapToResponse(bloodTypeRepository.save(bloodType), components);
    }

}
