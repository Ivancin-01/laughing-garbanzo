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

    public void registrar(Usuario u) {
        // 1. Encriptar el campo pwd
        u.setPwd(encoder.encode(u.getPwd()));

        // 2. Establecer la fecha de creación (si no, dará error por nullable = false)
        u.setFCreacion(LocalDateTime.now());

        usuarioRepository.save(u);
    }
}