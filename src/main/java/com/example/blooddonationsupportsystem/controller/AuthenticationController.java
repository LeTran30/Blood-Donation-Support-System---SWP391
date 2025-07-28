package com.example.blooddonationsupportsystem.controller;


import com.example.blooddonationsupportsystem.dtos.request.user.Authentication;
import com.example.blooddonationsupportsystem.dtos.request.user.RegisterRequest;
import com.example.blooddonationsupportsystem.dtos.responses.ResponseObject;
import com.example.blooddonationsupportsystem.dtos.responses.user.AuthenticationResponse;
import com.example.blooddonationsupportsystem.dtos.responses.user.RegisterResponse;
import com.example.blooddonationsupportsystem.service.user.IUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@CrossOrigin("*")
public class AuthenticationController {

    private final IUserService userService;
    private final LogoutHandler logoutHandler;

    @PostMapping()
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        return userService.register(registerRequest);
    }

    @PostMapping("/staff")
    public ResponseEntity<RegisterResponse> registerStaff(@Valid @RequestBody RegisterRequest registerRequest) {
        return userService.registerStaff(registerRequest);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@Valid @RequestBody Authentication authentication) {
        return userService.login(authentication);
    }

    @PostMapping("/refreshToken")
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws Exception {
        userService.refresh(request, response);
    }

    @PostMapping("/logout")
    public ResponseEntity<ResponseObject> logout(HttpServletRequest request, HttpServletResponse response) {
        try {
            logoutHandler.logout(request, response, SecurityContextHolder.getContext().getAuthentication());
            SecurityContextHolder.clearContext();
            return ResponseEntity.ok(
                    ResponseObject.builder()
                            .status(HttpStatus.valueOf(HttpStatus.OK.value()))
                            .message("Logout successful")
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ResponseObject.builder()
                            .status(HttpStatus.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()))
                            .message("Logout failed")
                            .build()
            );
        }
    }

//    @PutMapping("/forgot-password")
//    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
//        return new ResponseEntity<>(userService.forgotPassword(email), HttpStatus.OK);
//    }

    @PutMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam String email,
                                           @RequestParam String otp,
                                           @RequestParam String password) {
        return  userService.resetPassword(email, otp,password);
    }

    @PostMapping("/google-login")
    public ResponseEntity<?> loginWithGoogle(@RequestParam String idTokenString) {
        return userService.loginWithGoogle(idTokenString);
    }   

}
