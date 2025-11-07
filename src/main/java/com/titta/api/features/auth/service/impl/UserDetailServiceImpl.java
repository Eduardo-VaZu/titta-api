// Ubicación: com/titta/api/service/auth/UserDetailServiceImpl.java

package com.titta.api.features.auth;

import com.titta.api.features.auth.dto.AuthLoginRequest;
import com.titta.api.features.auth.dto.AuthRegisterRequest;
import com.titta.api.features.auth.dto.AuthResponse;
import com.titta.api.config.exception.DuplicateResourceException;
import com.titta.api.domain.model.CredencialTradicional;
import com.titta.api.domain.model.Rol;
import com.titta.api.domain.model.Usuario;
import com.titta.api.domain.model.enums.RolEnum;
import com.titta.api.domain.repository.RolRepository;
import com.titta.api.domain.repository.UsuarioRepository;
import com.titta.api.config.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserDetailServiceImpl implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RolRepository rolRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("El usuario " + username + " no existe."));

        List<SimpleGrantedAuthority> authorityList = new ArrayList<>();
        authorityList.add(new SimpleGrantedAuthority("ROLE_".concat(usuario.getRol().getNombreRol().name())));

        return new User(
                usuario.getEmail(),
                usuario.getCredencialTradicional().getPasswordHash(),
                usuario.isEstadoUsuario(),
                true,
                true,
                true,
                authorityList
        );
    }

    public AuthResponse registerUser(AuthRegisterRequest registerRequest) {
        if (usuarioRepository.findByEmail(registerRequest.email()).isPresent()) {
            throw new DuplicateResourceException("El correo electrónico ya está registrado.");
        }

        Rol defaultRol = rolRepository.findByNombreRol(RolEnum.CLIENTE)
                .orElseThrow(() -> new RuntimeException("Error interno: El rol CLIENTE no se encuentra."));

        Usuario usuario = Usuario.builder()
                .nombre(registerRequest.nombre())
                .apellidoPaterno(registerRequest.apellidoPaterno())
                .apellidoMaterno(registerRequest.apellidoMaterno())
                .email(registerRequest.email())
                .estadoUsuario(true)
                .rol(defaultRol)
                .build();

        CredencialTradicional credencial = CredencialTradicional.builder()
                .usuario(usuario)
                .passwordHash(passwordEncoder.encode(registerRequest.password()))
                .build();

        usuario.setCredencialTradicional(credencial);

        Usuario usuarioCreado = usuarioRepository.save(usuario);

        AuthResponse.UsuarioResponseDto  usuarioDto = new AuthResponse.UsuarioResponseDto(
                usuario.getIdUsuario(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getRol().getNombreRol().name(),
                usuario.isEstadoUsuario()
        );

        return new AuthResponse(usuarioDto, null, "Usuario logueado exitosamente", true);
    }

    public AuthResponse loginUser(AuthLoginRequest authLoginRequest) {
        String username = authLoginRequest.username();
        String password = authLoginRequest.password();

        Authentication authentication = this.authenticate(username, password);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = this.jwtUtils.createToken(authentication);

        Usuario usuario = usuarioRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("El usuario " + username + " no existe."));

        AuthResponse.UsuarioResponseDto  usuarioDto = new AuthResponse.UsuarioResponseDto(
                usuario.getIdUsuario(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getRol().getNombreRol().name(),
                usuario.isEstadoUsuario()
        );

        return new AuthResponse(usuarioDto, accessToken, "Usuario logueado exitosamente", true);
    }

    private Authentication authenticate(String username, String password) {
        UserDetails userDetails = this.loadUserByUsername(username);

        if (userDetails == null) {
            throw new BadCredentialsException("Usuario no encontrado.");
        }

        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("Contraseña inválida.");
        }

        return new UsernamePasswordAuthenticationToken(username, userDetails.getPassword(), userDetails.getAuthorities());
    }
}