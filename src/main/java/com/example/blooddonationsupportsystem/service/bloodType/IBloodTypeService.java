package com.example.blooddonationsupportsystem.service.bloodType;

import com.example.blooddonationsupportsystem.dtos.responses.bloodType.BloodTypeResponse;

import java.util.List;

public interface IBloodTypeService {
    List<BloodTypeResponse> getAllBloodTypes();
}
