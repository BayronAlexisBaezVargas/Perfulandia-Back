package com.example.BackPerfulandia.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authz -> authz
                        // Permite que CUALQUIERA acceda a estas rutas:
                        .requestMatchers("/api/productos/**").permitAll() // Ver productos
                        .requestMatchers("/api/auth/**").permitAll()    // Login y Registro
                        .requestMatchers("/api/usuarios/**").permitAll() // Gestionar perfiles (¡Temporal!)
                        .requestMatchers("/api/pedidos/**").permitAll() // Gestionar pedidos (¡Temporal!)

                        // CAMBIAR A .authenticated() cuando implementes JWT
                        .anyRequest().permitAll()
                );

        return http.build();
    }
}