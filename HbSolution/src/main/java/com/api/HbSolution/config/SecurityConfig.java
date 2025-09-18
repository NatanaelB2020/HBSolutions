package com.api.HbSolution.config;

import com.api.HbSolution.security.JwtAuthenticationFilter;
import com.api.HbSolution.service.UsuarioDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final UsuarioDetailsService usuarioDetailsService;
    private final JwtAuthenticationFilter jwtFilter;

    public SecurityConfig(UsuarioDetailsService usuarioDetailsService,
                          JwtAuthenticationFilter jwtFilter) {
        this.usuarioDetailsService = usuarioDetailsService;
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // login/signup ficam liberados
                        .requestMatchers("/auth/**").permitAll()
                        // suas APIs exigem token
                        .requestMatchers("/api/clientes/**",
                                         "/api/empresas/**",
                                         "/api/enderecos/**",
                                         "/api/produtos/**",
                                         "/api/usuarios/**").authenticated()
                        .anyRequest().permitAll()
                )
                // adiciona o filtro JWT antes do filtro padrão de autenticação
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig)
            throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
