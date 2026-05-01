package com.tfg.gestion_practicas.services;

import com.tfg.gestion_practicas.model.Usuario;
import com.tfg.gestion_practicas.repository.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// import com.tfg.gestion_practicas.repository.AlumnoRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Spring Security llama a este método con lo que el usuario escriba en el campo
     * "username" del formulario de login.
     *
     * Buscamos primero por correo (flujo principal) y, si no existe,
     * intentamos por username — así el formulario acepta ambos formatos.
     */
    @Override
    public UserDetails loadUserByUsername(String input) throws UsernameNotFoundException {

        Usuario user = usuarioRepository.findByCorreo(input)
                .or(() -> usuarioRepository.findByUsername(input))
                .orElseThrow(() -> new UsernameNotFoundException(
                        "No se encontró ningún usuario con: " + input));

        return User.builder()
                .username(user.getCorreo()) // Usamos el correo como identificador interno
                .password(user.getPwd())
                .roles(user.getRol().name()) // Genera "ROLE_ALUMNO", "ROLE_TUTOR", etc.
                .build();
    }
}