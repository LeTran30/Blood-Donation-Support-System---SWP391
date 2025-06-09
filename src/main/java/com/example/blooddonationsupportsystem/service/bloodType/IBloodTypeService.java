package com.example.blooddonationsupportsystem.service.bloodType;

import com.example.blooddonationsupportsystem.dtos.responses.bloodType.BloodTypeResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IBloodTypeService {
    Page<BloodTypeResponse> getAllBloodTypes(int page, int size);
}
