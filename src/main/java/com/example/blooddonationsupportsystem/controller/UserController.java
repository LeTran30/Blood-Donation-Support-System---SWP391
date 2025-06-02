package com.example.blooddonationsupportsystem.controller;


import com.example.blooddonationsupportsystem.dtos.responses.user.ListUserResponse;
import com.example.blooddonationsupportsystem.service.user.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final IUserService userService;

    @GetMapping("getUser")
    @PreAuthorize("hasAuthority('admin:read')")
    public ResponseEntity<ListUserResponse> getUser() {
        return userService.getListUser();
    }


}


