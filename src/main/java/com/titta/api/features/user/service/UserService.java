package com.titta.api.features.user.service;

import com.titta.api.features.user.dto.request.AdminUserUpdateRoleRequestDto;
import com.titta.api.features.user.dto.request.AdminUserUpdateStatusRequestDto;
import com.titta.api.features.user.dto.request.UserUpdateProfileRequestDto;
import com.titta.api.features.user.dto.response.UserResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    UserResponseDto getMyProfile();

    UserResponseDto updateMyProfile(UserUpdateProfileRequestDto requestDto);

    Page<UserResponseDto> getAllUsers(Pageable pageable);

    UserResponseDto updateUserRole(Long userId, AdminUserUpdateRoleRequestDto requestDto);

    UserResponseDto updateUserStatus(Long userId, AdminUserUpdateStatusRequestDto requestDto);
}
