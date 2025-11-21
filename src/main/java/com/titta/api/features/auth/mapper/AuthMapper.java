package com.titta.api.features.auth.mapper;

import com.titta.api.domain.model.Usuario;
import com.titta.api.features.auth.dto.response.AuthLoginResponse;
import org.springframework.stereotype.Component;

@Component
public class AuthMapper {

    public AuthLoginResponse.UsuarioResponseDto toUsuarioLoginResponseDto(Usuario usuario) {
        if (usuario == null) {
            return null;
        }

        if (usuario.getRol() == null || usuario.getRol().getNombreRol() == null) {
            throw new IllegalStateException("El usuario " + usuario.getEmail() + " no tiene un rol asignado.");
        }

        return new AuthLoginResponse.UsuarioResponseDto(
                usuario.getIdUsuario(),
                usuario.getNombre() + " " + usuario.getApellidoPaterno() + " " + usuario.getApellidoMaterno(),
                usuario.getEmail(),
                usuario.getRol().getNombreRol().name(),
                true
        );
    }

}