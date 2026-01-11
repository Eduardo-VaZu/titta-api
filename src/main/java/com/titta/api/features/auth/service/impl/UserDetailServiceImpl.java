package com.titta.api.features.auth.service.impl;

import com.titta.api.domain.model.Permiso;
import com.titta.api.domain.model.Usuario;
import com.titta.api.domain.repository.UsuarioRepository;

import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("El usuario " + username + " no existe."));

        List<SimpleGrantedAuthority> authorityList = new ArrayList<>();
        authorityList.add(new SimpleGrantedAuthority("ROLE_".concat(usuario.getRol().getNombreRol().name())));

        if (usuario.getRol().getPermisos() != null) {
            for (Permiso permiso : usuario.getRol().getPermisos()) {
                authorityList.add(new SimpleGrantedAuthority(permiso.getNombre()));
            }
        }

        if (usuario.getCredencialTradicional() == null) {
            throw new UsernameNotFoundException("El usuario no tiene credenciales tradicionales.");
        }

        if (!usuario.isEstadoUsuario()) {
            throw new DisabledException("La cuenta está deshabilitada. Contacta al administrador.");
        }

        return new User(
                usuario.getEmail(),
                usuario.getCredencialTradicional().getPasswordHash(),
                usuario.isEstadoUsuario(),
                true,
                true,
                true,
                authorityList);
    }
}