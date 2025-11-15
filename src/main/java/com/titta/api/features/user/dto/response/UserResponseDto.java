package com.titta.api.features.user.dto.response;

public record UserResponseDto(
        Long id,
        String nombre,
        String apellidoPaterno,
        String apellidoMaterno,
        String email,
        String rol,
        boolean estadoUsuario
) {
}