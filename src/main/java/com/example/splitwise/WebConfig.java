package com.example.splitwise;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT); // Pretty-print JSON
        return mapper;
    }
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
