package com.tfg.gestion_practicas.services;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tfg.gestion_practicas.model.Alumno;
import com.tfg.gestion_practicas.model.Rol;
import com.tfg.gestion_practicas.model.Usuario;
import com.tfg.gestion_practicas.repository.AlumnoRepository;
import com.tfg.gestion_practicas.repository.UsuarioRepository;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AlumnoRepository alumnoRepository;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @Transactional
    public Usuario registrar(Usuario u, String matricula, String dni) {

        if (u == null) throw new RuntimeException("Los datos del usuario no son válidos");

        if (usuarioRepository.existsByCorreo(u.getCorreo()))
            throw new RuntimeException("El correo ya está en uso");

        if (usuarioRepository.existsByUsername(u.getUsername()))
            throw new RuntimeException("El nombre de usuario ya está en uso");

        if (u.getRol() == null) throw new RuntimeException("Debes seleccionar un rol");

        // Encriptamos la contraseña antes de guarda
        u.setPwd(encoder.encode(u.getPwd()));
        u.setFCreacion(LocalDateTime.now());
        u.setActivo(true);

        Usuario usuarioGuardado = usuarioRepository.save(u);

   
        if (usuarioGuardado.getRol() == Rol.ALUMNO) {
            Alumno nuevoAlumno = new Alumno();
            nuevoAlumno.setUsuario(usuarioGuardado);
            nuevoAlumno.setDni(dni);
            nuevoAlumno.setMatricula(matricula);
            alumnoRepository.save(nuevoAlumno);
        }

        return usuarioGuardado;
    }
}