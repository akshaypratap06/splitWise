package com.example.splitwise;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Allow CORS for Angular's default development port (4200)
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:4200")  // Allow requests from Angular
                .allowedMethods("GET", "POST", "PUT", "DELETE","PATCH") // Specify allowed HTTP methods
                .allowedHeaders("*") // Allow any headers
                .allowCredentials(true); // Allow cookies or credentials to be sent
    }
}
