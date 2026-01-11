package com.titta.api.features.user.service.impl;

import com.titta.api.config.exception.ResourceNotFoundException;
import com.titta.api.domain.model.Rol;
import com.titta.api.domain.model.Usuario;
import com.titta.api.domain.model.enums.RolEnum;
import com.titta.api.domain.repository.RolRepository;
import com.titta.api.domain.repository.UsuarioRepository;
import com.titta.api.features.user.dto.request.AdminUserUpdateRoleRequestDto;
import com.titta.api.features.user.dto.request.AdminUserUpdateStatusRequestDto;
import com.titta.api.features.user.dto.request.UserUpdateProfileRequestDto;
import com.titta.api.features.user.dto.response.UserResponseDto;
import com.titta.api.features.user.mapper.UserMapper;
import com.titta.api.features.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getMyProfile() {
        Usuario usuario = getAuthenticatedUser();
        return userMapper.toUserResponseDto(usuario);
    }

    @Override
    public UserResponseDto updateMyProfile(UserUpdateProfileRequestDto requestDto) {
        Usuario usuario = getAuthenticatedUser();

        usuario.setNombre(requestDto.nombre());
        usuario.setApellidoPaterno(requestDto.apellidoPaterno());
        usuario.setApellidoMaterno(requestDto.apellidoMaterno());

        Usuario usuarioActualizado = usuarioRepository.save(usuario);
        log.info("Perfil actualizado para el usuario: {}", usuario.getEmail());

        return userMapper.toUserResponseDto(usuarioActualizado);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponseDto> getAllUsers(Pageable pageable) {
        Page<Usuario> usuarios = usuarioRepository.findAll(pageable);
        return userMapper.toUserResponseDtoPage(usuarios);
    }

    @Override
    public UserResponseDto updateUserRole(Long userId, AdminUserUpdateRoleRequestDto requestDto) {
        Usuario usuario = findUserById(userId);

        RolEnum nuevoRolEnum = RolEnum.valueOf(requestDto.rolNombre().toUpperCase());
        Rol nuevoRol = rolRepository.findByNombreRol(nuevoRolEnum)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado: " + requestDto.rolNombre()));

        usuario.setRol(nuevoRol);
        Usuario usuarioActualizado = usuarioRepository.save(usuario);
        log.warn("ADMIN: Rol cambiado a {} para el usuario {}", nuevoRol.getNombreRol(), usuario.getEmail());

        return userMapper.toUserResponseDto(usuarioActualizado);
    }

    @Override
    public UserResponseDto updateUserStatus(Long userId, AdminUserUpdateStatusRequestDto requestDto) {
        Usuario usuario = findUserById(userId);

        usuario.setEstadoUsuario(requestDto.estado());
        Usuario usuarioActualizado = usuarioRepository.save(usuario);
        log.warn("ADMIN: Estado cambiado a {} para el usuario {}", requestDto.estado(), usuario.getEmail());

        return userMapper.toUserResponseDto(usuarioActualizado);
    }

    private Usuario getAuthenticatedUser() {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuario no encontrado en el contexto de seguridad: " + userEmail));
    }

    private Usuario findUserById(Long userId) {
        return usuarioRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + userId));
    }
}