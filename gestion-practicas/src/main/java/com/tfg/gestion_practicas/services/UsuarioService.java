package com.tfg.gestion_practicas.services;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.tfg.gestion_practicas.model.Usuario;
import com.tfg.gestion_practicas.repository.UsuarioRepository;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private BCryptPasswordEncoder encoder;

    public Usuario registrar(Usuario u) {

        if (u == null) {
            throw new RuntimeException("Los datos del usuario no son válidos");
        }

        // Comprobamos si ya existe un usuario con ese correo.
        if (usuarioRepository.existsByCorreo(u.getCorreo())) {
            throw new RuntimeException("El correo ya está en uso");
        }

        // Comprobamos si ya existe un usuario con ese username.
        if (usuarioRepository.existsByUsername(u.getUsername())) {
            throw new RuntimeException("El nombre de usuario ya está en uso");
        }

        // Comprobación simple del rol.
        if (u.getRol() == null) {
            throw new RuntimeException("Debes seleccionar un rol");
        }

        // Encriptamos la contraseña.
        u.setPwd(encoder.encode(u.getPwd()));

        // Guardamos la fecha de creación automáticamente.
        u.setFCreacion(LocalDateTime.now());

        // Marcamos al usuario como activo.
        u.setActivo(true);

        return usuarioRepository.save(u);
    }
}