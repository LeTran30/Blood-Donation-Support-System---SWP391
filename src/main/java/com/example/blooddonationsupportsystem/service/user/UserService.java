package com.example.blooddonationsupportsystem.service.user;

import com.example.blooddonationsupportsystem.dtos.request.user.Authentication;
import com.example.blooddonationsupportsystem.dtos.request.user.RegisterRequest;
import com.example.blooddonationsupportsystem.dtos.responses.ResponseObject;
import com.example.blooddonationsupportsystem.dtos.responses.user.AuthenticationResponse;
import com.example.blooddonationsupportsystem.dtos.responses.user.ListUserResponse;
import com.example.blooddonationsupportsystem.dtos.responses.user.RegisterResponse;
import com.example.blooddonationsupportsystem.dtos.responses.user.UserResponse;
import com.example.blooddonationsupportsystem.models.Token;
import com.example.blooddonationsupportsystem.models.User;
import com.example.blooddonationsupportsystem.repositories.TokenRepository;
import com.example.blooddonationsupportsystem.repositories.UserRepository;
import com.example.blooddonationsupportsystem.service.jwt.IJwtService;
import com.example.blooddonationsupportsystem.utils.Role;
import com.example.blooddonationsupportsystem.utils.TokenType;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final IJwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final TokenRepository tokenRepository;
    private final ModelMapper modelMapper;
    private final OtpService otpService;

    @Override
    public ResponseEntity<RegisterResponse> register(RegisterRequest registerRequest) {
        try {
            if (userRepository.existsByEmail(registerRequest.getEmail())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        RegisterResponse.builder()
                                .status(HttpStatus.BAD_REQUEST.value())
                                .message("Email already in use")
                                .build()
                );
            }

            User newUser = User.builder()
                    .email(registerRequest.getEmail())
                    .password(passwordEncoder.encode(registerRequest.getPassword()))
                    .role(Role.MEMBER)
                    .fullName(registerRequest.getFullName())
                    .phoneNumber(registerRequest.getPhoneNumber())
                    .status(true)
                    .build();

            userRepository.save(newUser);
            return ResponseEntity.ok(RegisterResponse.builder()
                    .status(HttpStatus.OK.value())
                    .message("User registered successfully")
                    .build());

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    RegisterResponse.builder()
                            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .message("Registration failed")
                            .build()
            );
        }
    }

    @Override
    public ResponseEntity<AuthenticationResponse> login(Authentication authenticationRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authenticationRequest.getEmail(),
                            authenticationRequest.getPassword()
                    )
            );

            User user = userRepository.findByEmail(authenticationRequest.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            String jwtToken = jwtService.generateToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);

            revokeAllUserTokens(user);
            saveToken(user, jwtToken, refreshToken);
            UserResponse userResponse = modelMapper.map(user, UserResponse.class);
            AuthenticationResponse.ResponseData responseData = AuthenticationResponse.ResponseData.builder()
                    .user(userResponse)
                    .token(jwtToken)
                    .refreshToken(refreshToken)
                    .build();

            return ResponseEntity.ok(AuthenticationResponse.builder()
                    .status(HttpStatus.OK.value())
                    .message("Login successful")
                    .data(responseData)
                    .build());

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    AuthenticationResponse.builder()
                            .status(HttpStatus.NOT_FOUND.value())
                            .message("Your email or password is incorrect")
                            .build()
            );
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    AuthenticationResponse.builder()
                            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .message("An error occurred")
                            .build()
            );
        }
    }

    @Override
    public void refresh(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUserEmail(refreshToken);
        if (userEmail != null) {
            var user = this.userRepository.findByEmail(userEmail).orElseThrow();
            if (jwtService.isTokenValid(refreshToken, user)) {
                var newToken = jwtService.generateToken(user);
                revokeAllUserTokens(user);
                saveToken(user, newToken, refreshToken);
                var authResponse = AuthenticationResponse.builder()
                        .status(200)
                        .message("Successfully")
                        .data(AuthenticationResponse.ResponseData.builder()
                                .token(newToken)
                                .refreshToken(refreshToken)
                                .user(null)
                                .build())
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }

    @Override
    public ResponseEntity<ListUserResponse> getListUser() {
        try {
            // Lấy token từ header Authorization
            String authHeader = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes()))
                    .getRequest().getHeader("Authorization");

            // Kiểm tra nếu Authorization header không tồn tại hoặc không hợp lệ
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ListUserResponse.builder()
                                .users(null)
                                .message("Missing or invalid Authorization header")
                                .build());
            }

            // Trích xuất token từ header
            String token = authHeader.substring(7);
            String userEmail = jwtService.extractUserEmail(token);

            // Tìm người dùng dựa trên email từ token
            var requester = userRepository.findUserByEmail(userEmail).orElse(null);

            if (requester == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ListUserResponse.builder()
                                .users(null)
                                .message("Unauthorized request - User not found")
                                .build());
            }

            // Kiểm tra quyền admin
            if (!Role.ADMIN.equals(requester.getRole())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ListUserResponse.builder()
                                .users(null)
                                .message("Access denied - Admin role required")
                                .build());
            }

            // Lấy danh sách người dùng nếu người yêu cầu có quyền admin
            List<User> users = userRepository.findAll();
            List<UserResponse> userResponses = users.stream()
                    .map(user -> modelMapper.map(user, UserResponse.class))
                    .collect(Collectors.toList());

            return ResponseEntity.status(HttpStatus.OK)
                    .body(ListUserResponse.builder()
                            .users(userResponses)
                            .message("Success")
                            .build());

        } catch (Exception e) {
            e.printStackTrace();
            // Trả về lỗi 500 nếu có exception xảy ra
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ListUserResponse.builder()
                            .users(null)
                            .message("An error occurred: " + e.getMessage())
                            .build());
        }
    }

//    @Override
//    public ResponseEntity<?> forgotPassword(String email) {
//        try {
//            emailConfig.sendSetPassword(email);
//        } catch (jakarta.mail.MessagingException e) {
//            throw new RuntimeException("Unable to send set password email, please try again", e);
//        }
//
//        return ResponseEntity.status(HttpStatus.OK).body(ResponseObject.builder()
//                .status(HttpStatus.OK)
//                .message("Success")
//                .data(ForgotPassword.builder()
//                        .note("Please check your mail")
//                        .build())
//                .build());
//    }

    @Override
    public ResponseEntity<?> resetPassword(String email, String otp, String password) {
        try {
            if (email.isEmpty() || otp.isEmpty() || password.isEmpty()) {
                return ResponseEntity.ok().body(ResponseObject.builder()
                        .status(HttpStatus.valueOf(HttpStatus.NO_CONTENT.value()))
                        .message("Please enter email, OTP, and new password")
                        .build());
            }
            if (!otpService.isValidOtp(email, otp)) {
                return ResponseEntity.ok().body(ResponseObject.builder()
                        .status(HttpStatus.valueOf(HttpStatus.BAD_REQUEST.value()))
                        .message("Invalid otp")
                        .build());
            }
            Optional<User> optionalUser = userRepository.findByEmail(email);
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                user.setPassword(passwordEncoder.encode(password));
                userRepository.save(user);
                return ResponseEntity.ok().body(ResponseObject.builder()
                        .status(HttpStatus.valueOf(HttpStatus.OK.value()))
                        .message("Reset password successful")
                        .build());
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()))
                    .message(e.getMessage())
                    .build());
        }
        return ResponseEntity.badRequest().body(ResponseObject.builder()
                .status(HttpStatus.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()))
                .message("Loi ")
                .build());
    }



    private void saveToken(User user, String jwtToken, String refreshToken) {
        Token token = Token.builder()
                .user(user)
                .token(jwtToken)
                .refreshToken(refreshToken)
                .tokenType(TokenType.BEARER)
                .revoked(false)
                .expired(false)
                .build();
        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(User user) {
        var tokenList = tokenRepository.findAllUserTokenByUserId(user.getId());
        tokenList.forEach(token -> {
            token.setRevoked(true);
            token.setExpired(true);
        });
        tokenRepository.saveAll(tokenList);
    }

}
