package com.tfg.gestion_practicas.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/usuarios/**").permitAll() // Abierto para el registro
                        .anyRequest().permitAll() // TEMPORAL: Abre todo para probar
                )
                .formLogin(form -> form.disable()) // Desactiva el formulario de login de Spring
                .httpBasic(basic -> basic.disable()); // Desactiva la autenticación básica

        return http.build();
    }
}