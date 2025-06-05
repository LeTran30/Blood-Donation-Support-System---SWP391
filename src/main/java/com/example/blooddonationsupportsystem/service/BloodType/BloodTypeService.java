package com.example.blooddonationsupportsystem.service.BloodType;

import com.example.blooddonationsupportsystem.dtos.responses.bloodType.BloodTypeResponse;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.blooddonationsupportsystem.repositories.BloodTypeRepository;
import com.example.blooddonationsupportsystem.models.BloodType;

import java.util.stream.Collectors;

import java.util.List;

@Service
public class BloodTypeService implements IBloodTypeService {
    @Autowired
    private BloodTypeRepository bloodTypeRepository;

    @Override
    public List<BloodTypeResponse> getAllBloodTypes() {
        return bloodTypeRepository.findAll().stream()
                .map(bloodType -> BloodTypeResponse.builder()
                        .typeName(bloodType.getTypeName())
                        .build())
                .collect(Collectors.toList());
    }
}
