// Ubicación: com/titta/api/service/auth/UserDetailServiceImpl.java

package com.titta.api.service.auth;

import com.titta.api.dto.auth.AuthLoginRequest;
import com.titta.api.dto.auth.AuthRegisterRequest;
import com.titta.api.dto.auth.AuthResponse;
import com.titta.api.exception.DuplicateResourceException;
import com.titta.api.model.CredencialTradicional;
import com.titta.api.model.Rol;
import com.titta.api.model.Usuario;
import com.titta.api.model.enums.RolEnum;
import com.titta.api.repository.RolRepository;
import com.titta.api.repository.UsuarioRepository;
import com.titta.api.util.JwtUtils;
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

    // Inyectamos los beans que necesitamos
    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RolRepository rolRepository;

    // Este es el método que ya tenías del paso anterior
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

    // ### AÑADE ESTE NUEVO MÉTODO PARA EL REGISTRO ###
    public AuthResponse registerUser(AuthRegisterRequest registerRequest) {
        // 1. Validamos que el email no esté ya registrado para evitar duplicados.
        if (usuarioRepository.findByEmail(registerRequest.email()).isPresent()) {
            throw new DuplicateResourceException("El correo electrónico ya está registrado.");
        }

        // 2. Buscamos el rol por defecto para un nuevo usuario (en este caso, CLIENTE).
        Rol defaultRol = rolRepository.findByNombreRol(RolEnum.CLIENTE)
                .orElseThrow(() -> new RuntimeException("Error interno: El rol CLIENTE no se encuentra."));

        // 3. Creamos la entidad Usuario con los datos del DTO.
        Usuario usuario = Usuario.builder()
                .nombre(registerRequest.nombre())
                .apellidoPaterno(registerRequest.apellidoPaterno())
                .apellidoMaterno(registerRequest.apellidoMaterno())
                .email(registerRequest.email())
                .estadoUsuario(true) // Activamos el usuario por defecto.
                .rol(defaultRol)
                .build();

        // 4. Creamos la credencial, encriptando la contraseña que nos llega.
        CredencialTradicional credencial = CredencialTradicional.builder()
                .usuario(usuario)
                .passwordHash(passwordEncoder.encode(registerRequest.password()))
                .build();

        // 5. Establecemos la relación bidireccional entre Usuario y Credencial.
        usuario.setCredencialTradicional(credencial);

        // 6. Guardamos el usuario. Gracias a la cascada (cascade), la credencial se guardará automáticamente.
        Usuario usuarioCreado = usuarioRepository.save(usuario);

        // 7. Devolvemos una respuesta exitosa. No devolvemos un token para forzar al usuario a hacer login.
        return new AuthResponse(usuarioCreado.getEmail(), "Usuario registrado exitosamente", null, true);
    }

    /**
     * Procesa la solicitud de login, autentica al usuario y genera un token.
     */
    public AuthResponse loginUser(AuthLoginRequest authLoginRequest) {
        String username = authLoginRequest.username();
        String password = authLoginRequest.password();

        // 1. Autentica al usuario
        Authentication authentication = this.authenticate(username, password);

        // 2. Si la autenticación es exitosa, la guarda en el contexto de seguridad
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 3. Genera el token JWT
        String accessToken = this.jwtUtils.createToken(authentication);

        // 4. Devuelve la respuesta con el token
        return new AuthResponse(username, "Usuario logueado exitosamente", accessToken, true);
    }

    /**
     * Valida las credenciales del usuario.
     */
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