package com.titta.api.features.user.controller;

import com.titta.api.features.user.dto.request.UserUpdateProfileRequestDto;
import com.titta.api.features.user.dto.response.UserResponseDto;
import com.titta.api.features.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponseDto> getMyProfile() {
        return ResponseEntity.ok(userService.getMyProfile());
    }

    @PutMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponseDto> updateMyProfile(
            @Valid @RequestBody UserUpdateProfileRequestDto requestDto) {
        return ResponseEntity.ok(userService.updateMyProfile(requestDto));
    }
}