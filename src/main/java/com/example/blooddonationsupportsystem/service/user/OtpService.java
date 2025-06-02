package com.example.blooddonationsupportsystem.service.user;

import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class OtpService {
    private final ConcurrentHashMap<String, String> otpStorage = new ConcurrentHashMap<>();
    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    public void saveOtp(String email, String otp) {
        otpStorage.put(email, otp);
        executorService.schedule(() -> {
            otpStorage.remove(email);
        }, 5000, TimeUnit.MINUTES);

    }

    public String getOtp(String email) {
        return otpStorage.get(email);
    }

    public boolean isValidOtp(String email, String otp) {
        String storedOtp = getOtp(email);
        return otp.equals(storedOtp);
    }
}
