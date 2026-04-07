package com.tfg.gestion_practicas.services;

import com.tfg.gestion_practicas.model.Rol;
import com.tfg.gestion_practicas.model.TutorCentro;
import com.tfg.gestion_practicas.model.Usuario;
import com.tfg.gestion_practicas.model.Centro;
import com.tfg.gestion_practicas.repository.TutorCentroRepository;
import com.tfg.gestion_practicas.repository.UsuarioRepository;
import com.tfg.gestion_practicas.repository.CentroRepository; // Necesitarás este repo
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class TutorCentroService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TutorCentroRepository tutorCentroRepository;

    @Autowired
    private CentroRepository centroRepository;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @Transactional
    public void registrarTutor(Usuario u, String telefono, Long idCentro) {
        
        // 1. Validaciones de Usuario (Email y Username únicos)
        if (usuarioRepository.existsByCorreo(u.getCorreo())) {
            throw new RuntimeException("El correo ya está en uso");
        }
        if (usuarioRepository.existsByUsername(u.getUsername())) {
            throw new RuntimeException("El nombre de usuario ya está en uso");
        }

        // 2. Configurar y guardar Usuario
        u.setPwd(encoder.encode(u.getPwd()));
        u.setFCreacion(LocalDateTime.now());
        u.setActivo(true);
        u.setRol(Rol.TUTOR_CENTRO); // Asignamos el rol automáticamente
        
        Usuario usuarioGuardado = usuarioRepository.save(u);

        // 3. Crear el perfil de TutorCentro
        TutorCentro tutor = new TutorCentro();
        tutor.setUsuario(usuarioGuardado); // @MapsId hará que el ID sea el mismo
        tutor.setTelefono(telefono);
        
        // 4. Vincular con el Centro
        if (idCentro != null) {
            Centro centro = centroRepository.findById(idCentro)
                .orElseThrow(() -> new RuntimeException("El centro seleccionado no existe"));
            tutor.setCentro(centro);
        }

        tutorCentroRepository.save(tutor);
    }
}