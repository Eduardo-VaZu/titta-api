package com.titta.api.features.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;

@Builder
@JsonPropertyOrder({"usuario", "jwt", "message", "status"})
public record AuthLoginResponse(
        UsuarioResponseDto usuario,
        String jwt,
        String message,
        boolean status
) {
    @JsonPropertyOrder({"id", "nombre", "email", "rol", "estadoUsuario"})
    public record UsuarioResponseDto(
            Long id,
            String nombre,
            String email,
            String rol,
            boolean estadoUsuario
    ) {
    }
}

