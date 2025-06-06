package com.example.blooddonationsupportsystem.service.eligibilityCheck;

import com.example.blooddonationsupportsystem.dtos.request.eligibilityCheck.EligibilityCheckRequest;
import com.example.blooddonationsupportsystem.dtos.responses.eligibilityCheck.EligibilityCheckResponse;
import com.example.blooddonationsupportsystem.models.EligibilityCheck;
import com.example.blooddonationsupportsystem.models.User;
import com.example.blooddonationsupportsystem.repositories.EligibilityCheckRepository;
import com.example.blooddonationsupportsystem.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EligibilityCheckService implements IEligibilityCheckService {
    private final EligibilityCheckRepository eligibilityCheckRepository;
    private final UserRepository userRepository;

    @Override
    public void createEligibilityCheck(EligibilityCheckRequest eligibilityCheckRequest) {
        User user = userRepository.findById(eligibilityCheckRequest.getUser())
                .orElseThrow(() -> new RuntimeException(" User not found with id : " + eligibilityCheckRequest.getUser()));
        EligibilityCheck eligibilityCheck = EligibilityCheck.builder()
                .user(user)
                .isEligible(eligibilityCheckRequest.getIsEligible())
                .checkDate(eligibilityCheckRequest.getCheckDate())
                .reason(eligibilityCheckRequest.getReason())
                .build();
        eligibilityCheckRepository.save(eligibilityCheck);
    }

    @Override
    public EligibilityCheckResponse getEligibilityCheckByUserId(Integer userId) {
        EligibilityCheck eligibilityCheck = eligibilityCheckRepository.findByUserId(userId);
        if (eligibilityCheck == null) {
            throw new RuntimeException("Eligibility check not found for user id: " + userId);
        }

        return EligibilityCheckResponse.builder()
                .checkId(eligibilityCheck.getCheckId())
                .isEligible(eligibilityCheck.getIsEligible())
                .checkDate(eligibilityCheck.getCheckDate())
                .reason(eligibilityCheck.getReason())
                .user(eligibilityCheck.getUser().getId())
                .build();
    }
}
