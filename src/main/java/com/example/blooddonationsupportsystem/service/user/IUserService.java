package com.example.blooddonationsupportsystem.service.user;

import com.example.blooddonationsupportsystem.dtos.request.user.Authentication;
import com.example.blooddonationsupportsystem.dtos.request.user.RegisterRequest;
import com.example.blooddonationsupportsystem.dtos.responses.user.AuthenticationResponse;
import com.example.blooddonationsupportsystem.dtos.responses.user.ListUserResponse;
import com.example.blooddonationsupportsystem.dtos.responses.user.RegisterResponse;
import com.example.blooddonationsupportsystem.dtos.request.user.UpdateUserRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public interface IUserService {
    ResponseEntity<RegisterResponse> register(@Valid RegisterRequest registerRequest);

    ResponseEntity<AuthenticationResponse> login(@Valid Authentication authentication);

    void refresh(HttpServletRequest request, HttpServletResponse response) throws IOException;

    ResponseEntity<ListUserResponse> getListUser();

//    ResponseEntity<?> forgotPassword(String email);

    ResponseEntity<?> resetPassword(String email, String otp, String password);

    ResponseEntity<?> getCurrentUserInfo();

    ResponseEntity<?> getUserById(Integer id);

    ResponseEntity<?> updateUser(Integer id, UpdateUserRequest request);

    ResponseEntity<?> findNearbyDonors(Double lat, Double lon, Double radiusKm);

}
