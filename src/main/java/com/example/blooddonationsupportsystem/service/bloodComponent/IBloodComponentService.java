package com.example.blooddonationsupportsystem.service.bloodComponent;

import com.example.blooddonationsupportsystem.dtos.request.bloodComponent.BloodComponentRequest;
import com.example.blooddonationsupportsystem.dtos.responses.bloodComponent.BloodComponentResponse;
import org.springframework.data.domain.Page;

import java.awt.print.Pageable;
import java.util.List;

public interface IBloodComponentService {
    Page<BloodComponentResponse> getAllBloodComponents(int page,int size);
    void createBloodComponent(BloodComponentRequest request);
    void updateBloodComponent(Integer id, BloodComponentRequest request);
    void deleteBloodComponent(Integer id);
}
