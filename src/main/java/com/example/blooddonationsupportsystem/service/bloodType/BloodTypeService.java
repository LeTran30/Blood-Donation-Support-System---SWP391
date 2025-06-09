package com.example.blooddonationsupportsystem.service.bloodType;

import com.example.blooddonationsupportsystem.dtos.responses.bloodType.BloodTypeResponse;
import com.example.blooddonationsupportsystem.models.BloodType;
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
}
