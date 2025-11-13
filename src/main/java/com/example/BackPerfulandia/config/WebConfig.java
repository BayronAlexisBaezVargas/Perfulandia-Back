package com.example.BackPerfulandia.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// @Configuration le dice a Spring que esta clase contiene configuraciones
@Configuration
public class WebConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**") // Aplica a todas las rutas bajo /api/
                        .allowedOrigins("https://perfulandia-frontend-react.vercel.app")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Métodos HTTP permitidos
                        .allowedHeaders("*")  // Permite todas las cabeceras (headers)
                        .allowCredentials(false); // No se permiten credenciales (cookies) por defecto
            }
        };
    }
}
