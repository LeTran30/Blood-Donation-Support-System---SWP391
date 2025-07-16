package com.example.blooddonationsupportsystem.service.user;

import com.example.blooddonationsupportsystem.dtos.request.user.Authentication;
import com.example.blooddonationsupportsystem.dtos.request.user.RegisterRequest;
import com.example.blooddonationsupportsystem.dtos.request.user.UpdateUserRequest;
import com.example.blooddonationsupportsystem.dtos.responses.ResponseObject;
import com.example.blooddonationsupportsystem.dtos.responses.user.*;
import com.example.blooddonationsupportsystem.models.BloodType;
import com.example.blooddonationsupportsystem.models.Token;
import com.example.blooddonationsupportsystem.models.User;
import com.example.blooddonationsupportsystem.repositories.BloodTypeRepository;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
import java.util.Map;
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
    private final BloodTypeRepository bloodTypeRepository;

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
    public ResponseEntity<?> getListUser(int page, int size) {
        try {
            PageRequest pageRequest = PageRequest.of(page, size);
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
            Page<User> users = userRepository.findAll(pageRequest);
            List<UserResponse> userResponses = users.stream()
                    .map(user -> modelMapper.map(user, UserResponse.class))
                    .collect(Collectors.toList());
            return ResponseEntity.status(HttpStatus.OK)
                    .body(ResponseObject.builder()
                            .status(HttpStatus.OK)
                            .message("Users retrieved successfully")
                            .data(Map.of(
                                    "content", ListUserResponse.builder()
                                            .users(userResponses).
                                            build(),
                                    "page", Map.of(
                                            "size", users.getSize(),
                                            "number", users.getNumber(),
                                            "totalElements", users.getTotalElements(),
                                            "totalPages", users.getTotalPages()
                                    )
                            ))
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

    @Override
    public ResponseEntity<?> getCurrentUserInfo() {
        try {
            // Lấy token từ header Authorization
            String authHeader = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes()))
                    .getRequest().getHeader(HttpHeaders.AUTHORIZATION);

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ResponseObject.builder()
                                .status(HttpStatus.BAD_REQUEST)
                                .message("Missing or invalid Authorization header")
                                .build());
            }

            // Trích xuất token
            String token = authHeader.substring(7);
            String userEmail = jwtService.extractUserEmail(token);

            // Tìm user
            User user = userRepository.findUserByEmail(userEmail).orElse(null);

            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ResponseObject.builder()
                                .status(HttpStatus.UNAUTHORIZED)
                                .message("User not found")
                                .build());
            }

            UserDetailResponse response =
                UserDetailResponse.builder()
                                  .id(user.getId())
                                  .fullName(user.getFullName())
                                  .email(user.getEmail())
                                  .role(user.getRole())
                                  .status(user.isStatus())
                                  .phoneNumber(user.getPhoneNumber())
                                  .address(user.getAddress())
                                  .gender(user.getGender())
                                  .latitude(user.getLatitude())
                                  .longitude(user.getLongitude())
                                  .dateOfBirth(user.getDateOfBirth())
                                  .bloodTypeId(user.getBloodType() != null ? user.getBloodType().getBloodTypeId() : null) // ✅ An toàn
                                  .build(); 


            return ResponseEntity.ok(ResponseObject.builder()
                    .status(HttpStatus.OK)
                    .message("Success")
                    .data(response)
                    .build());

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseObject.builder()
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .message("Internal server error")
                            .build());
        }
    }


    @Override
    public ResponseEntity<?> getUserById(Integer id) {
        try {
            Optional<User> optionalUser = userRepository.findById(id);
            if (optionalUser.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ResponseObject.builder()
                                .status(HttpStatus.NOT_FOUND)
                                .message("User not found")
                                .build());
            }

            UserResponse userResponse = modelMapper.map(optionalUser.get(), UserResponse.class);
            return ResponseEntity.ok(ResponseObject.builder()
                    .status(HttpStatus.OK)
                    .message("Success")
                    .data(userResponse)
                    .build());

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseObject.builder()
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .message("Error: " + e.getMessage())
                            .build());
        }
    }


    @Override
    public ResponseEntity<?> updateUser(Integer id, UpdateUserRequest request) {
        try {
            Optional<User> optionalUser = userRepository.findById(id);
            if (optionalUser.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ResponseObject.builder()
                                .status(HttpStatus.NOT_FOUND)
                                .message("User not found")
                                .build());
            }

            User user = optionalUser.get();

            if (request.getFullName() != null) {
                user.setFullName(request.getFullName());
            }
            if (request.getPhoneNumber() != null) {
                user.setPhoneNumber(request.getPhoneNumber());
            }
            if (request.getAddress() != null) {
                user.setAddress(request.getAddress());
            }
            if (request.getGender() != null) {
                user.setGender(request.getGender());
            }
            if (request.getDateOfBirth() != null) {
                user.setDateOfBirth(request.getDateOfBirth());
            }
            if (request.getLatitude() != null) {
                user.setLatitude(request.getLatitude());
            }
            if (request.getLongitude() != null) {
                user.setLongitude(request.getLongitude());
            }
            if (request.getAddress() != null) {
                user.setAddress(request.getAddress());
            }
            if (request.getBloodTypeId() != null) {
            Optional<BloodType> optionalBloodType = bloodTypeRepository.findById(request.getBloodTypeId());
            if (optionalBloodType.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid blood type ID");
            }
            user.setBloodType(optionalBloodType.get());
            }

            userRepository.save(user);
            UserDetailResponse response = 
                UserDetailResponse.builder()
                                  .id(user.getId())
                                  .fullName(user.getFullName())
                                  .email(user.getEmail())
                                  .role(user.getRole())
                                  .status(user.isStatus())
                                  .phoneNumber(user.getPhoneNumber())
                                  .address(user.getAddress())
                                  .gender(user.getGender())
                                  .latitude(user.getLatitude())
                                  .longitude(user.getLongitude())
                                  .dateOfBirth(user.getDateOfBirth())
                                  .bloodTypeId(user.getBloodType() != null ? user.getBloodType().getBloodTypeId() : null) // ✅ An toàn
                                  .build();

            return ResponseEntity.ok(ResponseObject.builder()
                    .status(HttpStatus.OK)
                    .message("User updated successfully")
                    .data(response)
                    .build());

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseObject.builder()
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .message("Error: " + e.getMessage())
                            .build());
        }
    }


    @Override
    public ResponseEntity<?> findNearbyDonors(Double lat, Double lon, Double radiusKm, int page, int size) {
        try {
            PageRequest pageRequest = PageRequest.of(page, size);
            Page<User> nearbyUsers = userRepository.findUsersNearby(lat, lon, radiusKm, pageRequest);
            List<UserResponse> userResponses = nearbyUsers.stream()
                    .map(user -> modelMapper.map(user, UserResponse.class))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(ResponseObject.builder()
                    .status(HttpStatus.OK)
                    .message("Nearby donors found")
                    .data(Map.of(
                            "content", ListUserResponse.builder()
                                    .users(userResponses)
                                    .build(),
                            "page", Map.of(
                                    "size", nearbyUsers.getSize(),
                                    "number", nearbyUsers.getNumber(),
                                    "totalElements", nearbyUsers.getTotalElements(),
                                    "totalPages", nearbyUsers.getTotalPages()
                            )
                    ))
                    .build());

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseObject.builder()
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .message("Error: " + e.getMessage())
                            .build());
        }
    }

    @Override
    public ResponseEntity<RegisterResponse> registerStaff(RegisterRequest registerRequest) {
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
                    .role(Role.STAFF)
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
