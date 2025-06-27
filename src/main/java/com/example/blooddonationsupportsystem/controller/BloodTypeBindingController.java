package com.example.blooddonationsupportsystem.controller;

import com.example.blooddonationsupportsystem.dtos.responses.bloodComponent.BloodComponentResponse;
import com.example.blooddonationsupportsystem.models.BloodType;
import com.example.blooddonationsupportsystem.models.BloodComponent;
import com.example.blooddonationsupportsystem.repositories.BloodTypeRepository;
import com.example.blooddonationsupportsystem.repositories.BloodComponentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/blood-types")
@RequiredArgsConstructor
public class BloodTypeBindingController {

    private final BloodTypeRepository bloodTypeRepository;
    private final BloodComponentRepository bloodComponentRepository;

    // ✅ Gán danh sách component vào nhóm máu
    @PostMapping("/{id}/components")
    public Set<BloodComponentResponse> assignComponents(
            @PathVariable Integer id,
            @RequestBody List<Integer> componentIds) {

        BloodType bloodType = bloodTypeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("BloodType not found"));

        List<BloodComponent> components = bloodComponentRepository.findAllById(componentIds);
        bloodType.setComponents(Set.copyOf(components));
        bloodTypeRepository.save(bloodType);

        return components.stream()
                .map(c -> BloodComponentResponse.builder()
                        .componentId(c.getComponentId())
                        .componentName(c.getComponentName())
                        .build())
                .collect(Collectors.toSet());
    }

    // ✅ Xóa 1 component khỏi nhóm máu
    @DeleteMapping("/{bloodTypeId}/components/{componentId}")
    public void removeComponent(
            @PathVariable Integer bloodTypeId,
            @PathVariable Integer componentId) {

        BloodType bloodType = bloodTypeRepository.findById(bloodTypeId)
                .orElseThrow(() -> new EntityNotFoundException("BloodType not found"));

        BloodComponent component = bloodComponentRepository.findById(componentId)
                .orElseThrow(() -> new EntityNotFoundException("BloodComponent not found"));

        bloodType.getComponents().remove(component);
        bloodTypeRepository.save(bloodType);
    }

    // ✅ Lấy danh sách component của nhóm máu
    @GetMapping("/{id}/components")
    public Set<BloodComponentResponse> getComponents(@PathVariable Integer id) {
        BloodType bloodType = bloodTypeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("BloodType not found"));

        return bloodType.getComponents().stream()
                .map(c -> BloodComponentResponse.builder()
                        .componentId(c.getComponentId())
                        .componentName(c.getComponentName())
                        .build())
                .collect(Collectors.toSet());
    }
}
