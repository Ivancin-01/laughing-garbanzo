package com.tfg.gestion_practicas.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

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

    // Buscamos un alumno a partir del ID del usuario. 
    public Alumno obtenerPorUsuario(Long usuarioId) {
      Alumno alumno = alumnoRepository.findByUsuarioId(usuarioId).orElse(null);

      // Si no existe, lanzamos un error sencillo. 
      if (alumno == null) {
        throw new RuntimeException("Alumno no encontrado por el ID de usuario.");
      }

      return alumno;
    }

    // Nuevo método: Buscar por email un usuario. 
    public Alumno buscarPorEmail(String email) {
      return alumnoRepository.findByUsuarioCorreo(email).orElseThrow(() -> new RuntimeException("Alumno no encontrado con email: " + email));
    }

    @Transactional
    public void guardar(Alumno alumno) {
        alumnoRepository.saveAndFlush(alumno);
    }

    public void guardarCvAlumno(MultipartFile cv, String username) throws IOException {
      if (cv.isEmpty()) {
          throw new IllegalArgumentException("El archivo está vacío.");
      }

      String nombreOriginal = cv.getOriginalFilename();

      if(nombreOriginal == null || nombreOriginal.isBlank()) {
        throw new IllegalArgumentException("Nombre de archivo no válido.");
      }
      
      String extension = nombreOriginal.substring(nombreOriginal.lastIndexOf(".") + 1).toLowerCase();

      if (!extension.equals("pdf") && !extension.equals("doc") && !extension.equals("docx")) {
          throw new IllegalArgumentException("Formato no permitido.");
      }

      String nombreLimpio = nombreOriginal
        .replace(" ", "_")
        .replaceAll("[^a-zA-Z0-9._-]", "");

      String nombreArchivo = System.currentTimeMillis() + "_" + nombreLimpio;

      Path carpetaDestino = Paths.get("uploads/cv");
      Files.createDirectories(carpetaDestino);

      Path rutaArchivo = carpetaDestino.resolve(nombreArchivo);
      Files.copy(cv.getInputStream(), rutaArchivo, StandardCopyOption.REPLACE_EXISTING);

      Alumno alumno = alumnoRepository.findByUsuarioCorreo(username)
              .orElseThrow(() -> new RuntimeException("Alumno no encontrado"));

      alumno.setCvUrl("/uploads/cv/" + nombreArchivo);
      alumnoRepository.save(alumno);
  }

  public void cambiarPassword(String correo, String passwordActual, String passwordNueva, String passwordConfirmacion) {
    Alumno alumno = buscarPorEmail(correo);

    if(!passwordEncoder.matches(passwordActual, alumno.getUsuario().getPwd())) {
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

  public void actualizarPreferencias(String correo, Boolean perfilVisible, Boolean notificacionesEmail) {
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
