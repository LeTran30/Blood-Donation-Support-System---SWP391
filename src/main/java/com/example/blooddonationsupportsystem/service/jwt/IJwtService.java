package com.example.blooddonationsupportsystem.service.jwt;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

public interface IJwtService {
    String extractUserEmail(String token);

    boolean isTokenValid(String token, UserDetails userDetails);

    String generateToken(UserDetails userDetails);

    String generateRefreshToken(UserDetails userDetails);

    boolean isTokenExpired(String token);

    void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication);
}
