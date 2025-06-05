package com.example.blooddonationsupportsystem.configuration;


import com.example.blooddonationsupportsystem.repositories.TokenRepository;
import com.example.blooddonationsupportsystem.service.jwt.IJwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {
    private final IJwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final TokenRepository tokenRepository;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            String email = jwtService.extractUserEmail(token);

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                logger.info("Email: " + email);
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                boolean validToken = tokenRepository.findByToken(token)
                        .map(t -> !t.isExpired() && !t.isRevoked())
                        .orElse(false);

                if (jwtService.isTokenValid(token, userDetails) && validToken) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    logger.info("Authentication token details: " + authToken.getDetails());
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    logger.warn("Invalid token: " + token);
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}


