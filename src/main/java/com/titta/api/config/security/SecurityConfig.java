package com.titta.api.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.titta.api.config.security.filter.JwtTokenValidator;
import com.titta.api.config.security.oauth2.OAuth2LoginSuccessHandler;
import com.titta.api.config.security.jwt.JwtUtils;
import com.titta.api.domain.repository.TokenBlacklistRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtils jwtUtils;
    private final TokenBlacklistRepository tokenBlacklistRepository;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity, ObjectMapper objectMapper)
            throws Exception {
        return httpSecurity
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .httpBasic(httpBasic -> httpBasic.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> {

                    auth.requestMatchers("/api/v1/auth/**").permitAll();
                    auth.requestMatchers(
                            "/swagger-ui/**",
                            "/swagger-ui.html",
                            "/v3/api-docs/**",
                            "/swagger-resources/**",
                            "/webjars/**").permitAll();

                    auth.requestMatchers("/oauth2/**", "/login/**").permitAll();

                    auth.requestMatchers(HttpMethod.GET, "/api/v1/categorias/**").permitAll();
                    auth.requestMatchers(HttpMethod.GET, "/api/v1/inventario/sede/**").permitAll();
                    auth.requestMatchers(HttpMethod.GET, "/api/v1/productos/**").permitAll();
                    auth.requestMatchers(HttpMethod.GET, "/api/v1/sedes/**").permitAll();

                    auth.requestMatchers(HttpMethod.GET, "/api/v1/users/me").authenticated();
                    auth.requestMatchers(HttpMethod.PUT, "/api/v1/users/me").authenticated();

                    auth.requestMatchers("/api/v1/admin/users/**").hasRole("ADMINISTRADOR");

                    auth.requestMatchers(HttpMethod.POST, "/api/v1/categorias")
                            .hasRole("ADMINISTRADOR");
                    auth.requestMatchers(HttpMethod.PUT, "/api/v1/categorias/**")
                            .hasRole("ADMINISTRADOR");
                    auth.requestMatchers(HttpMethod.PUT,
                            "/api/v1/categorias/{idCategoria}/desactivar")
                            .hasRole("ADMINISTRADOR");

                    auth.requestMatchers(HttpMethod.POST, "/api/v1/productos")
                            .hasRole("ADMINISTRADOR");
                    auth.requestMatchers(HttpMethod.POST, "/api/v1/productos/batch")
                            .hasRole("ADMINISTRADOR");
                    auth.requestMatchers(HttpMethod.PUT, "/api/v1/productos/**")
                            .hasRole("ADMINISTRADOR");
                    auth.requestMatchers(HttpMethod.PUT,
                            "/api/v1/productos/{idProducto}/desactivar")
                            .hasRole("ADMINISTRADOR");

                    auth.requestMatchers(HttpMethod.POST, "/api/v1/sedes").hasRole("ADMINISTRADOR");
                    auth.requestMatchers(HttpMethod.PUT, "/api/v1/sedes/**")
                            .hasRole("ADMINISTRADOR");
                    auth.requestMatchers(HttpMethod.PUT, "/api/v1/sedes/{idSede}/desactivar")
                            .hasRole("ADMINISTRADOR");

                    auth.requestMatchers(HttpMethod.POST, "/api/v1/inventario/producto/**")
                            .hasAnyRole("ADMINISTRADOR", "EMPLEADO");

                    auth.requestMatchers(HttpMethod.GET, "/api/v1/cart").hasRole("CLIENTE");
                    auth.requestMatchers(HttpMethod.POST, "/api/v1/cart/add").hasRole("CLIENTE");
                    auth.requestMatchers(HttpMethod.PUT, "/api/v1/cart/item/{idProducto}")
                            .hasRole("CLIENTE");
                    auth.requestMatchers(HttpMethod.DELETE, "/api/v1/cart/item/{idProducto}")
                            .hasRole("CLIENTE");
                    auth.requestMatchers(HttpMethod.DELETE, "/api/v1/cart/clear")
                            .hasRole("CLIENTE");

                    auth.anyRequest().authenticated();
                })
                .oauth2Login(oauth2Login -> oauth2Login.successHandler(oAuth2LoginSuccessHandler))
                .addFilterBefore(new JwtTokenValidator(
                        jwtUtils, objectMapper,
                        tokenBlacklistRepository),
                        BasicAuthenticationFilter.class)
                .build();
    }
}
