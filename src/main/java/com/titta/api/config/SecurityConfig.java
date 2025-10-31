package com.titta.api.config;

import com.titta.api.config.filter.JwtTokenValidator;
import com.titta.api.service.auth.UserDetailServiceImpl;
import com.titta.api.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration // Indica que esta clase contiene configuración para Spring.
@EnableWebSecurity // Habilita la seguridad web de Spring Security.
@EnableMethodSecurity // Habilita el uso de anotaciones como @PreAuthorize en tus controladores.
public class SecurityConfig {

    @Autowired
    private JwtUtils jwtUtils;

    /**
     * Define la cadena de filtros de seguridad. Aquí es donde se configuran las reglas principales.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(csrf -> csrf.disable()) // Deshabilitamos CSRF porque usaremos JWT (API stateless).
                .httpBasic(Customizer.withDefaults()) // Habilitamos la autenticación HTTP Básica.
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // ¡Clave! Configuramos la API para que sea sin estado.
                // ### AÑADE ESTA SECCIÓN DE AUTORIZACIÓN ###
                .authorizeHttpRequests(auth -> {
                    // Permitimos el acceso sin autenticación a todos los endpoints bajo /api/v1/auth/
                    auth.requestMatchers("/api/v1/auth/**").permitAll();
                    // Para cualquier otra petición, requerimos que el usuario esté autenticado.
                    auth.anyRequest().authenticated();
                })
                .addFilterBefore(new JwtTokenValidator(jwtUtils), BasicAuthenticationFilter.class) // Añadimos nuestro filtro validador de JWT ANTES del filtro de autenticación básica.
                .build();
    }

    /**
     * El AuthenticationManager es el encargado de orquestar el proceso de autenticación.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * Define el "proveedor de autenticación". Le dice a Spring cómo obtener los detalles del usuario
     * y cómo verificar la contraseña.
     */
    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailServiceImpl userDetailService) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder()); // Le decimos qué codificador de contraseñas usar.
        provider.setUserDetailsService(userDetailService); // Le decimos dónde encontrar los usuarios (nuestro UserDetailServiceImpl).
        return provider;
    }

    /**
     * Define el bean para el codificador de contraseñas. Usamos BCrypt, que es el estándar actual.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}