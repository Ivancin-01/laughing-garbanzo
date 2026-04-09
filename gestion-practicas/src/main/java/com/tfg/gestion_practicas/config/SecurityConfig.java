package com.tfg.gestion_practicas.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

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
            // Aseguramos que todas las rutas de alumno y recursos estáticos sean libres
            .requestMatchers("/", "/login", "/usuarios/**", "/css/**", "/js/**", "/img/**", "/alumno/**", "/ofertas/**").permitAll()
            .anyRequest().authenticated())
        .formLogin(form -> form
            .loginPage("/login")
            .loginProcessingUrl("/login")
            .defaultSuccessUrl("/alumno/dashboard", true)
            .failureUrl("/login?error")
            .permitAll()
        )
        .logout(logout -> logout
            .logoutUrl("/logout").logoutSuccessUrl("/login?logout").permitAll()
        );
            
        return http.build();
    }
}