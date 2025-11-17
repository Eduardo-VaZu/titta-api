package com.titta.api.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.titta.api.config.filter.JwtTokenValidator;
import com.titta.api.config.util.JwtUtils;
import com.titta.api.domain.repository.TokenBlacklistRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
public class SecurityConfig {

    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private TokenBlacklistRepository tokenBlacklistRepository;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity, ObjectMapper objectMapper) throws Exception {
        return httpSecurity
                .csrf(csrf -> csrf
                        .disable())
                .cors(Customizer
                        .withDefaults())
                .httpBasic(Customizer
                        .withDefaults())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> {

                    auth.requestMatchers("/api/v1/auth/**").permitAll();
                    auth.requestMatchers("/v3/api-docs/**").permitAll();
                    auth.requestMatchers("/swagger-ui.html").permitAll();
                    auth.requestMatchers("/swagger-ui/**").permitAll();

                    auth.requestMatchers(HttpMethod.GET, "/api/v1/categorias/**").permitAll();
                    auth.requestMatchers(HttpMethod.GET, "/api/v1/inventario/sede/**").permitAll();
                    auth.requestMatchers(HttpMethod.GET, "/api/v1/productos/**").permitAll();
                    auth.requestMatchers(HttpMethod.GET, "/api/v1/sedes/**").permitAll();


                    auth.requestMatchers(HttpMethod.GET, "/api/v1/users/me").authenticated();
                    auth.requestMatchers(HttpMethod.PUT, "/api/v1/users/me").authenticated();

                    auth.requestMatchers("/api/v1/admin/users/**").hasRole("ADMINISTRADOR");

                    auth.requestMatchers(HttpMethod.POST, "/api/v1/categorias").hasRole("ADMINISTRADOR");
                    auth.requestMatchers(HttpMethod.PUT, "/api/v1/categorias/**").hasRole("ADMINISTRADOR");
                    auth.requestMatchers(HttpMethod.PUT, "/api/v1/categorias/{idCategoria}/desactivar").hasRole("ADMINISTRADOR");

                    auth.requestMatchers(HttpMethod.POST, "/api/v1/productos").hasRole("ADMINISTRADOR");
                    auth.requestMatchers(HttpMethod.POST, "/api/v1/productos/batch").hasRole("ADMINISTRADOR");
                    auth.requestMatchers(HttpMethod.PUT, "/api/v1/productos/**").hasRole("ADMINISTRADOR");
                    auth.requestMatchers(HttpMethod.PUT, "/api/v1/productos/{idProducto}/desactivar").hasRole("ADMINISTRADOR");

                    auth.requestMatchers(HttpMethod.POST, "/api/v1/sedes").hasRole("ADMINISTRADOR");
                    auth.requestMatchers(HttpMethod.PUT, "/api/v1/sedes/**").hasRole("ADMINISTRADOR");
                    auth.requestMatchers(HttpMethod.PUT, "/api/v1/sedes/{idSede}/desactivar").hasRole("ADMINISTRADOR");

                    auth.requestMatchers(HttpMethod.POST, "/api/v1/inventario/producto/**")
                            .hasAnyRole("ADMINISTRADOR", "EMPLEADO");

                    auth.requestMatchers(HttpMethod.GET, "/api/v1/cart").hasRole("CLIENTE");
                    auth.requestMatchers(HttpMethod.POST, "/api/v1/cart/add").hasRole("CLIENTE");
                    auth.requestMatchers(HttpMethod.PUT, "/api/v1/cart/item/{idProducto}").hasRole("CLIENTE");
                    auth.requestMatchers(HttpMethod.DELETE, "/api/v1/cart/item/{idProducto}").hasRole("CLIENTE");
                    auth.requestMatchers(HttpMethod.DELETE, "/api/v1/cart/clear").hasRole("CLIENTE");

                    auth.anyRequest().authenticated();
                })
                .addFilterBefore(new JwtTokenValidator(jwtUtils, objectMapper, tokenBlacklistRepository), BasicAuthenticationFilter.class)
                .build();
    }
}