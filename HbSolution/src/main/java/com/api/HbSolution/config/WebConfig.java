package com.api.HbSolution.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Configura o CORS globalmente para permitir o acesso do frontend
        registry.addMapping("/**") // Permite CORS para todas as rotas
                .allowedOrigins("http://localhost:4200") // Permite apenas o acesso do frontend local
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true); // Permite o envio de credenciais (cookies, por exemplo)
    }
}
