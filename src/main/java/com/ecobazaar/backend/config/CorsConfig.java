package com.ecobazaar.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// @Configuration - DISABLED: Using SecurityConfig for CORS
public class CorsConfig implements WebMvcConfigurer {

    // @Override - DISABLED: Using SecurityConfig for CORS
    public void addCorsMappings(CorsRegistry registry) {
        // CORS configuration moved to SecurityConfig to avoid conflicts
        // registry.addMapping("/api/**")
        //         .allowedOrigins(
        //             "https://ecobazzarx.web.app",
        //             "https://ecobazzarx.firebaseapp.com",
        //             "http://localhost:3000",
        //             "http://localhost:8080",
        //             "http://127.0.0.1:3000",
        //             "http://127.0.0.1:8080"
        //         )
        //         .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
        //         .allowedHeaders("*")
        //         .allowCredentials(true)
        //         .maxAge(3600);
    }
}

