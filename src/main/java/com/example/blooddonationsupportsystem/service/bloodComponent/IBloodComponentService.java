package com.example.blooddonationsupportsystem.service.bloodComponent;

import com.example.blooddonationsupportsystem.dtos.responses.bloodComponent.BloodComponentResponse;

import java.util.List;

public interface IBloodComponentService {
    List<BloodComponentResponse> getAllBloodComponents();
}
