package com.tfg.gestion_practicas.services;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tfg.gestion_practicas.model.Usuario;
import com.tfg.gestion_practicas.model.Alumno; // Asegúrate de importar tu modelo Alumno
import com.tfg.gestion_practicas.model.Rol;
import com.tfg.gestion_practicas.repository.UsuarioRepository;
import com.tfg.gestion_practicas.repository.AlumnoRepository; // Asegúrate de importar tu repositorio Alumno

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // 1. Inyectamos el repositorio de Alumno
    @Autowired
    private AlumnoRepository alumnoRepository;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @Transactional
    public Usuario registrar(Usuario u, String matricula, String dni) { // 2. Añadimos el parámetro matricula

        if (u == null) {
            throw new RuntimeException("Los datos del usuario no son válidos");
        }

        if (usuarioRepository.existsByCorreo(u.getCorreo())) {
            throw new RuntimeException("El correo ya está en uso");
        }

        if (usuarioRepository.existsByUsername(u.getUsername())) {
            throw new RuntimeException("El nombre de usuario ya está en uso");
        }

        if (u.getRol() == null) {
            throw new RuntimeException("Debes seleccionar un rol");
        }

        // Encriptamos y preparamos el usuario
        u.setPwd(encoder.encode(u.getPwd()));
        u.setFCreacion(LocalDateTime.now());
        u.setActivo(true);

        // 3. Guardamos primero el Usuario para obtener su ID
        Usuario usuarioGuardado = usuarioRepository.save(u);

        // 4. Lógica para insertar en la tabla Alumno
        if (usuarioGuardado.getRol() == Rol.ALUMNO) {
            if (usuarioGuardado.getRol() == Rol.ALUMNO) {
                Alumno nuevoAlumno = new Alumno();

                // Vinculamos la relación (esto asigna el ID automáticamente por el @MapsId)
                nuevoAlumno.setUsuario(usuarioGuardado);

                // Cogemos los datos DIRECTAMENTE del usuario que acabamos de guardar
                // Asegúrate de tener estos campos (nombre, correo) en tu clase Alumno.java
                nuevoAlumno.setNombre(usuarioGuardado.getNombre());
                nuevoAlumno.setEmail(usuarioGuardado.getCorreo()); // O u.getEmail(), según tu atributo

                // Datos específicos del formulario
                nuevoAlumno.setDni(dni);
                nuevoAlumno.setMatricula(matricula);

                // Guardamos
                alumnoRepository.save(nuevoAlumno);
            }
        }

        return usuarioGuardado;
    }
}