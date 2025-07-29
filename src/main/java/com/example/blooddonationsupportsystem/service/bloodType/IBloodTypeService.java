package com.example.blooddonationsupportsystem.service.bloodType;

import com.example.blooddonationsupportsystem.dtos.request.bloodType.BloodTypeRequest;
import com.example.blooddonationsupportsystem.dtos.responses.bloodType.BloodTypeResponse;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
public interface IBloodTypeService {
    Page<BloodTypeResponse> getAllBloodTypes(int page, int size);
    BloodTypeResponse createBloodType(BloodTypeRequest request);
    BloodTypeResponse updateBloodType(Integer id, BloodTypeRequest request);
    void deleteBloodType(Integer id);

    @Transactional
    BloodTypeResponse assignComponentsToBloodType(Integer bloodTypeId, Collection<Integer> componentIds);

    @Transactional
    BloodTypeResponse removeComponentFromBloodType(Integer bloodTypeId, Integer componentId);
}
