package com.titta.api.config;

import com.titta.api.config.filter.JwtTokenValidator;
import com.titta.api.config.util.JwtUtils;
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

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .httpBasic(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> {

                    auth.requestMatchers("/api/v1/auth/**").permitAll();

                    auth.requestMatchers("/v3/api-docs/**").permitAll();
                    auth.requestMatchers("/swagger-ui.html").permitAll();
                    auth.requestMatchers("/swagger-ui/**").permitAll();

                    auth.requestMatchers(HttpMethod.GET, "/api/v1/productos/**").permitAll();
                    auth.requestMatchers(HttpMethod.GET, "/api/v1/categorias/**").permitAll();
                    auth.requestMatchers(HttpMethod.GET, "/api/v1/sedes/**").permitAll();

                    auth.requestMatchers(HttpMethod.POST, "/api/v1/inventario/productos/{idProducto}/stock/sede/{idSede}")
                            .hasAnyRole("ADMINISTRADOR", "VENDEDOR");

                    auth.requestMatchers(HttpMethod.PUT, "/api/v1/productos/{idProducto}/details")
                            .hasRole("ADMINISTRADOR");
                    auth.requestMatchers(HttpMethod.POST, "/api/v1/productos/{idProducto}/activate")
                            .hasRole("ADMINISTRADOR");
                    auth.requestMatchers(HttpMethod.POST, "/api/v1/productos/{idProducto}/deactivate")
                            .hasRole("ADMINISTRADOR");

                    auth.requestMatchers(HttpMethod.POST, "/api/v1/productos").hasRole("ADMINISTRADOR");
                    auth.requestMatchers(HttpMethod.POST, "/api/v1/sedes").hasRole("ADMINISTRADOR");
                    auth.requestMatchers(HttpMethod.POST, "/api/v1/categorias").hasRole("ADMINISTRADOR");

                    auth.anyRequest().authenticated();
                })
                .addFilterBefore(new JwtTokenValidator(jwtUtils), BasicAuthenticationFilter.class)
                .build();
    }
}