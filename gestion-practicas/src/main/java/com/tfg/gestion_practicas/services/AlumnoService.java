package com.tfg.gestion_practicas.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.tfg.gestion_practicas.model.Alumno;
import com.tfg.gestion_practicas.repository.AlumnoRepository;

import jakarta.transaction.Transactional;

@Service
public class AlumnoService {

  @Autowired
  private AlumnoRepository alumnoRepository;

  @Autowired
  private BCryptPasswordEncoder passwordEncoder;

  @Autowired
  private SupabaseStorageService supabaseStorageService;

  // Buscamos un alumno a partir del ID del usuario.
  public Alumno obtenerPorUsuario(Long usuarioId) {
    Alumno alumno = alumnoRepository.findByUsuarioId(usuarioId).orElse(null);

    if (alumno == null) {
      throw new RuntimeException("Alumno no encontrado por el ID de usuario.");
    }

    return alumno;
  }

  // Buscar alumno por correo del usuario logueado.
  public Alumno buscarPorEmail(String email) {
    return alumnoRepository.findByUsuarioCorreo(email)
        .orElseThrow(() -> new RuntimeException("Alumno no encontrado con email: " + email));
  }

  @Transactional
  public void guardar(Alumno alumno) {
    alumnoRepository.saveAndFlush(alumno);
  }

  @Transactional
  public void guardarCvAlumno(MultipartFile cv, String correoAlumno) {
    Alumno alumno = alumnoRepository.findByUsuarioCorreo(correoAlumno)
        .orElseThrow(() -> new RuntimeException("Alumno no encontrado."));

    String rutaCvSupabase = supabaseStorageService.subirCv(cv, alumno.getId());

    alumno.setCvUrl(rutaCvSupabase);

    alumnoRepository.save(alumno);
  }

  public void cambiarPassword(String correo,
      String passwordActual,
      String passwordNueva,
      String passwordConfirmacion) {
    Alumno alumno = buscarPorEmail(correo);

    if (!passwordEncoder.matches(passwordActual, alumno.getUsuario().getPwd())) {
      throw new RuntimeException("La contraseña actual no es correcta.");
    }

    if (!passwordNueva.equals(passwordConfirmacion)) {
      throw new RuntimeException("Las nuevas contraseñas no coinciden.");
    }

    if (passwordNueva.length() < 6) {
      throw new RuntimeException("La nueva contraseña debe tener al menos 6 caracteres.");
    }

    alumno.getUsuario().setPwd(passwordEncoder.encode(passwordNueva));

    alumnoRepository.save(alumno);
  }

  public void actualizarPreferencias(String correo,
      Boolean perfilVisible,
      Boolean notificacionesEmail) {
    Alumno alumno = buscarPorEmail(correo);

    alumno.setPerfilVisible(perfilVisible);
    alumno.setNotificacionesEmail(notificacionesEmail);

    alumnoRepository.save(alumno);
  }

  public void desactivarCuenta(String correo, String confirmacion) {
    if (!"DESACTIVAR".equals(confirmacion)) {
      throw new RuntimeException("Debes escribir DESACTIVAR para confirmar.");
    }

    Alumno alumno = buscarPorEmail(correo);

    alumno.getUsuario().setActivo(false);

    alumnoRepository.save(alumno);
  }
}