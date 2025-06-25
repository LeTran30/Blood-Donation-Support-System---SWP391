package com.example.blooddonationsupportsystem.service.bloodType;

import com.example.blooddonationsupportsystem.dtos.request.bloodType.BloodTypeRequest;
import com.example.blooddonationsupportsystem.dtos.responses.bloodType.BloodTypeResponse;
import com.example.blooddonationsupportsystem.models.BloodType;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.blooddonationsupportsystem.repositories.BloodTypeRepository;

import java.util.stream.Collectors;

import java.util.List;

@Service
public class BloodTypeService implements IBloodTypeService {
    @Autowired
    private BloodTypeRepository bloodTypeRepository;

    @Override
    public Page<BloodTypeResponse> getAllBloodTypes(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<BloodType> bloodPage = bloodTypeRepository.findAll(pageable);

        List<BloodTypeResponse> responseList = bloodTypeRepository.findAll().stream()
                .map(bloodType -> BloodTypeResponse.builder()
                        .typeName(bloodType.getTypeName())
                        .build())
                .toList();

        return new PageImpl<>(responseList, pageable, bloodPage.getTotalElements());
    }
    @Override
    public BloodTypeResponse createBloodType(BloodTypeRequest request) {
        BloodType bloodType = new BloodType();
        bloodType.setTypeName(request.getTypeName());

        bloodType = bloodTypeRepository.save(bloodType);

        return BloodTypeResponse.builder()
                .id(bloodType.getBloodTypeId())
                .typeName(bloodType.getTypeName())
                .build();
    }

    @Override
    public BloodTypeResponse updateBloodType(Integer id, BloodTypeRequest request) {
        BloodType bloodType = bloodTypeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("BloodType not found"));

        bloodType.setTypeName(request.getTypeName());
        bloodType = bloodTypeRepository.save(bloodType);

        return BloodTypeResponse.builder()
                .id(bloodType.getBloodTypeId())
                .typeName(bloodType.getTypeName())
                .build();
    }

    @Override
    public void deleteBloodType(Integer id) {
        if (!bloodTypeRepository.existsById(id)) {
            throw new EntityNotFoundException("BloodType not found");
        }
        bloodTypeRepository.deleteById(id);
    }
}
