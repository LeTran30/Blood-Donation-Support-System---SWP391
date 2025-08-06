package com.example.blooddonationsupportsystem.controller;


import com.example.blooddonationsupportsystem.dtos.responses.ResponseObject;
import com.example.blooddonationsupportsystem.dtos.request.user.UpdateUserRequest;
import com.example.blooddonationsupportsystem.models.User;
import com.example.blooddonationsupportsystem.repositories.UserRepository;
import com.example.blooddonationsupportsystem.service.user.IUserService;
import com.example.blooddonationsupportsystem.utils.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;


@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
@CrossOrigin("*")
public class UserController {
    private final IUserService userService;
    private final UserRepository userRepository;

    @GetMapping("getUser")
    @PreAuthorize("hasAuthority('admin:read')")
    public ResponseEntity<?> getUser(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return userService.getListUser(page, size);
    }

    @GetMapping("/me")
    @PreAuthorize("true")
    public ResponseEntity<?> getCurrentUser() {
        return userService.getCurrentUserInfo();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('admin:read', 'staff:read', 'member:read')")
    public ResponseEntity<?> getUserById(@PathVariable Integer id) {
        return userService.getUserById(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('admin:update', 'staff:update', 'member:update')")
    public ResponseEntity<?> updateUser(@PathVariable Integer id,
                                        @RequestBody UpdateUserRequest request,
                                        Principal principal) {

        User currentUser = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (Role.MEMBER.equals(currentUser.getRole()) && !currentUser.getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ResponseObject.builder()
                            .status(HttpStatus.FORBIDDEN)
                            .message("You do not have access to other users' data")
                            .build());
        }
        return userService.updateUser(id, request);
    }

    @GetMapping("/nearby")
    @PreAuthorize("true")
    public ResponseEntity<?> findNearbyDonors(
            @RequestParam Double lat, @RequestParam Double lon,
            @RequestParam(defaultValue = "10") Double radiusKm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size

    ) {
        return userService.findNearbyDonors(lat, lon, radiusKm, page, size);
    }

}


