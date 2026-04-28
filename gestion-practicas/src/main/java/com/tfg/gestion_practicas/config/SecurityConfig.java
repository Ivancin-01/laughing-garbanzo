package com.tfg.gestion_practicas.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.tfg.gestion_practicas.services.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ✅ Spring Security 7: el constructor exige UserDetailsService como argumento
    @Bean
    public DaoAuthenticationProvider authenticationProvider(BCryptPasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider(userDetailsService);
        auth.setPasswordEncoder(passwordEncoder);
        return auth;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, DaoAuthenticationProvider authenticationProvider) throws Exception {
        http
            .authenticationProvider(authenticationProvider)

            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/", "/login", "/usuarios/registro", "/usuarios/registrar",
                    "/css/**", "/js/**", "/img/**", "/json/**", "/error",
                    "/ofertas", "/ofertas/**"
                ).permitAll()

                .requestMatchers("/alumno/**").hasRole("ALUMNO")
                .requestMatchers("/tutor/**").hasRole("TUTOR")
                .requestMatchers("/tutor-centro/**").hasRole("TUTOR_CENTRO")
                .requestMatchers("/empresa/**").hasRole("EMPRESA")
                .requestMatchers("/admin/**").hasRole("ADMIN")

                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .usernameParameter("username")
                .passwordParameter("password")
                .successHandler((request, response, authentication) -> {
                    String role = authentication.getAuthorities().iterator().next().getAuthority();
                    switch (role) {
                        case "ROLE_ALUMNO":
                            response.sendRedirect("/alumno/dashboard");
                            break;
                        case "ROLE_TUTOR":
                            response.sendRedirect("/tutor/dashboard");
                            break;
                        case "ROLE_TUTOR_CENTRO":
                            response.sendRedirect("/tutor-centro/dashboard");
                            break;
                        case "ROLE_EMPRESA":
                            response.sendRedirect("/empresa/dashboard");
                            break;
                        case "ROLE_ADMIN":
                            response.sendRedirect("/admin/dashboard");
                            break;
                        default:
                            response.sendRedirect("/");
                    }
                })
                .failureUrl("/login?error")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .exceptionHandling(ex -> ex
                .accessDeniedPage("/error/403")
            )
            .csrf(csrf -> csrf.disable());

        return http.build();
    }
}