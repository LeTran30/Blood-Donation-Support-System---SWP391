package com.example.blooddonationsupportsystem.service.eligibilityCheck;

import com.example.blooddonationsupportsystem.dtos.request.eligibilityCheck.EligibilityCheckRequest;
import com.example.blooddonationsupportsystem.dtos.responses.eligibilityCheck.EligibilityCheckResponse;

public interface IEligibilityCheckService {
    void createEligibilityCheck(EligibilityCheckRequest eligibilityCheckRequest);
    EligibilityCheckResponse getEligibilityCheckByUserId(Integer userId);
}
