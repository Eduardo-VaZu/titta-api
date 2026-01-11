package com.titta.api.features.user.controller;

import com.titta.api.features.user.dto.request.AdminUserUpdateRoleRequestDto;
import com.titta.api.features.user.dto.request.AdminUserUpdateStatusRequestDto;
import com.titta.api.features.user.dto.response.UserResponseDto;
import com.titta.api.features.user.service.UserService;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/users")
@PreAuthorize("hasAuthority('GESTIONAR_USUARIOS')")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<Page<UserResponseDto>> getAllUsers(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(userService.getAllUsers(pageable));
    }

    @PutMapping("/{userId}/role")
    public ResponseEntity<UserResponseDto> updateUserRole(
            @PathVariable Long userId,
            @Valid @RequestBody AdminUserUpdateRoleRequestDto requestDto) {
        return ResponseEntity.ok(userService.updateUserRole(userId, requestDto));
    }

    @PutMapping("/{userId}/status")
    public ResponseEntity<UserResponseDto> updateUserStatus(
            @PathVariable Long userId,
            @Valid @RequestBody AdminUserUpdateStatusRequestDto requestDto) {
        return ResponseEntity.ok(userService.updateUserStatus(userId, requestDto));
    }
}