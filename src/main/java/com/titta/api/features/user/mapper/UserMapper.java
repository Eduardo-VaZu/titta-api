package com.titta.api.features.user.mapper;

import com.titta.api.domain.model.Usuario;
import com.titta.api.features.user.dto.response.UserResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    public UserResponseDto toUserResponseDto(Usuario usuario) {
        if (usuario == null) {
            return null;
        }

        if (usuario.getRol() == null || usuario.getRol().getNombreRol() == null) {
            throw new IllegalStateException("El usuario " + usuario.getEmail() + " no tiene un rol asignado.");
        }

        return new UserResponseDto(
                usuario.getIdUsuario(),
                usuario.getNombre(),
                usuario.getApellidoPaterno(),
                usuario.getApellidoMaterno(),
                usuario.getEmail(),
                usuario.getRol().getNombreRol().name(),
                usuario.isEstadoUsuario()
        );
    }

    public List<UserResponseDto> toUserResponseDtoList(List<Usuario> usuarios) {
        return usuarios.stream()
                .map(this::toUserResponseDto)
                .collect(Collectors.toList());
    }

    public Page<UserResponseDto> toUserResponseDtoPage(Page<Usuario> page) {
        return page.map(this::toUserResponseDto);
    }
}