package com.example.blooddonationsupportsystem.service.bloodCompoonent;

import com.example.blooddonationsupportsystem.dtos.responses.BloodComponent.BloodComponentResponse;

import java.util.List;

public interface IBloodComponentService {
    List<BloodComponentResponse> getAllBloodComponents();
}
