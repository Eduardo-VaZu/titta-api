package com.titta.api.config;

import com.titta.api.config.filter.JwtTokenValidator;
import com.titta.api.config.util.JwtUtils;
import com.titta.api.features.auth.service.impl.UserDetailServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtUtils jwtUtils;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(csrf -> csrf.disable())
                .httpBasic(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> {
                    // --- REGLAS DE AUTENTICACIÓN ---
                    auth.requestMatchers("/api/v1/auth/**").permitAll();

                    // --- REGLAS DE DOCUMENTACIÓN (NUEVAS) ---
                    auth.requestMatchers("/v3/api-docs/**").permitAll();
                    auth.requestMatchers("/swagger-ui.html").permitAll();
                    auth.requestMatchers("/swagger-ui/**").permitAll();

                    // --- REGLAS DE "LECTURA" PÚBLICAS (GET) ---
                    // Permitimos ver productos, categorías y sedes
                    auth.requestMatchers(HttpMethod.GET, "/api/v1/productos/**").permitAll();
                    auth.requestMatchers(HttpMethod.GET, "/api/v1/categorias/**").permitAll();
                    auth.requestMatchers(HttpMethod.GET, "/api/v1/sedes/**").permitAll();

                    // --- REGLAS DE "ESCRITURA" ESPECÍFICAS (¡Las nuevas!) ---
                    // Estas reglas van PRIMERO que las genéricas

                    // Solo VENDEDOR o ADMIN pueden ajustar stock
                    auth.requestMatchers(HttpMethod.POST, "/api/v1/productos/{idProducto}/stock/sede/{idSede}/adjust")
                            .hasAnyRole("ADMINISTRADOR", "VENDEDOR");

                    // Solo ADMIN puede cambiar detalles, activar o desactivar
                    auth.requestMatchers(HttpMethod.PUT, "/api/v1/productos/{idProducto}/details")
                            .hasRole("ADMINISTRADOR");
                    auth.requestMatchers(HttpMethod.POST, "/api/v1/productos/{idProducto}/activate")
                            .hasRole("ADMINISTRADOR");
                    auth.requestMatchers(HttpMethod.POST, "/api/v1/productos/{idProducto}/deactivate")
                            .hasRole("ADMINISTRADOR");

                    // Reglas de creación generales para ADMIN
                    auth.requestMatchers(HttpMethod.POST, "/api/v1/productos").hasRole("ADMINISTRADOR");
                    auth.requestMatchers(HttpMethod.POST, "/api/v1/sedes").hasRole("ADMINISTRADOR");
                    // (Categorias POST es público en tu código original, lo mantengo)
                    auth.requestMatchers(HttpMethod.POST, "/api/v1/categorias").permitAll();

                    // --- REGLA FINAL ---
                    // Cualquier otra petición que no coincida, debe estar autenticada
                    auth.anyRequest().authenticated();
                })
                .addFilterBefore(new JwtTokenValidator(jwtUtils), BasicAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailServiceImpl userDetailService) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(userDetailService);
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}