package com.developersarena.ecommerce.controller;

import com.developersarena.ecommerce.dto.UserProfileResponse;
import com.developersarena.ecommerce.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<UserProfileResponse> getProfile() {
        return ResponseEntity.ok(userService.getCurrentUserProfile());
    }

    @PutMapping
    public ResponseEntity<UserProfileResponse> updateProfile(@RequestBody UserProfileResponse updateRequest) {
        return ResponseEntity.ok(userService.updateUserProfile(updateRequest));
    }
 }
