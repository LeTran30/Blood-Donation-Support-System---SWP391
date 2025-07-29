package com.example.blooddonationsupportsystem.service.bloodComponent;

import com.example.blooddonationsupportsystem.dtos.request.bloodComponent.BloodComponentRequest;
import com.example.blooddonationsupportsystem.dtos.responses.bloodComponent.BloodComponentResponse;
import org.springframework.data.domain.Page;


public interface IBloodComponentService {
    Page<BloodComponentResponse> getAllBloodComponents(int page,int size);
    BloodComponentResponse createBloodComponent(BloodComponentRequest request);
    BloodComponentResponse updateBloodComponent(Integer id, BloodComponentRequest request);
    void deleteBloodComponent(Integer id);
}
