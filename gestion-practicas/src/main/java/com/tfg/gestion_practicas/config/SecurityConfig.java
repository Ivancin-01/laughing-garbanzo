package com.tfg.gestion_practicas.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

/* En esta clase configuramos lo siguiente en cuanto a SEGURIDAD:
    - Contraseñas: Con BCryptPasswordEncoder, las contraseñas se guardan cifradas.
    - Rutas: 
        - Públicas: login, registro, css, js e imágenes.
        - Privadas: dashboards y funciones internas de la página.
    - Utilizamos una página de login personalizada, donde el usuario puede cerrar sesión y recibir un mensaje visual. 
    - Todavía no limitamos por ROL pero dejamos todo preparado para ello. 
*/

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
                // Rutas públicas
                .requestMatchers(
                    "/", "/login", "/logout",
                    "/usuarios/registro", "/usuarios/registrar",
                    "/css/**", "/js/**", "/img/**", "/lib/**", "/ofertas/**"
                ).permitAll()
                // Rutas privadas por rol
                .requestMatchers("/alumno/**").hasRole("ALUMNO")
                .requestMatchers("/tutor/**").hasRole("TUTOR")
                .requestMatchers("/empresa/**").hasRole("EMPRESA")
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .successHandler(customSuccessHandler()) // ← redirige según rol
                .failureUrl("/login?error")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            );
 
        return http.build();
    }
 
@Bean
public AuthenticationSuccessHandler customSuccessHandler() {
    return (request, response, authentication) -> { 

        String rol = authentication.getAuthorities()
                .iterator().next()
                .getAuthority();

        String destino = switch (rol) {
            case "ROLE_ALUMNO"  -> "/alumno/dashboard";
            case "ROLE_TUTOR"   -> "/tutor/dashboard";
            case "ROLE_EMPRESA" -> "/empresa/dashboard";
            case "ROLE_ADMIN"   -> "/admin/dashboard";
            default             -> "/";
        };

        response.sendRedirect(destino);
    };
}
}