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
    private BCryptPasswordEncoder encoder; // Aquí Spring te "presta" la herramienta

    public Usuario registrar(Usuario u) {

        // Comprobamos si ya existe un usuario con un correo. 
        if (usuarioRepository.existsByCorreo(u.getCorreo())) {
            throw new RuntimeException("El correo ya está en uso");
        }

        // Encriptamos la contraseña para guardarla en la BBDD, guardamos la fecha de creación automáticamente y marcamos al usuario como activo.
        u.setPwd(encoder.encode(u.getPwd()));
        u.setFCreacion(LocalDateTime.now());
        u.setActivo(true);

        // Guardamos al usuario en la base de datos. 
        return usuarioRepository.save(u);
    }
}