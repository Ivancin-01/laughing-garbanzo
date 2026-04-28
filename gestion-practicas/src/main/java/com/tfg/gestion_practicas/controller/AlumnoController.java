package com.tfg.gestion_practicas.controller;

import com.tfg.gestion_practicas.services.AlumnoService;
import java.util.List;
import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.tfg.gestion_practicas.model.Alumno;
import com.tfg.gestion_practicas.model.Solicitud;
import com.tfg.gestion_practicas.repository.AlumnoRepository;
import com.tfg.gestion_practicas.repository.OfertaRepository;
import com.tfg.gestion_practicas.services.SolicitudService;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@Controller
public class AlumnoController {
    private final AuthController authController;
    private final AlumnoService alumnoService;

    @Autowired
    private SolicitudService solicitudService;

    @Autowired
    private AlumnoRepository alumnoRepository;

    @Autowired
    private OfertaRepository ofertaRepository;

    AlumnoController(AlumnoService alumnoService, AuthController authController) {
        this.alumnoService = alumnoService;
        this.authController = authController;
    }

    // Dashboard de alumno simplificada - para trabajar mejor.
    @GetMapping("/alumno/dashboard")
    public String dashboardAlumno(Model model, Principal principal) {
        // 1. Verificamos si existe una sesión que se encuentre activa.
        if (principal == null) {
            return "redirect:/login";
        }

        // 2. Obtenemos el alumno por su correo. Spring Security toma el email como dato
        // para identificar al usuario.
        String email = principal.getName();

        // 3. Buscamos al alumno en la DB por su email.
        Alumno al = alumnoRepository.findByUsuarioCorreo(email).orElse(null);

        // 3.1. Si el email del login no existe en la tabla alumnos, hacemos saltar una
        // nueva ventana.
        if (al == null) {
            return "redirect:/login?error=usuario-no-encontrado";
        }

        // 4. Cargamos las solicitudes reales del alumno encontrado.
        List<Solicitud> solicitudes = solicitudService.obtenerPorAlumno(al.getId());
        Long totalOfertas = ofertaRepository.count();

        // EXTRA: PARA LA BARRA DE PROGRESO DE LAS SOLICITUDES.
        int progreso = 0;
        if (al.getDni() != null && !al.getDni().isEmpty()) {
            progreso += 20;
        }

        if (al.getUsuario() != null) {
            if (al.getUsuario().getNombre() != null && !al.getUsuario().getNombre().isEmpty()) {
                progreso += 20;
            }
        }

        if (al.getUsuario().getCorreo() != null && !al.getUsuario().getCorreo().isEmpty()) {
            progreso += 20;
        }

        if (al.getMatricula() != null) {
            progreso += 20;
        }

        if (al.getCvUrl() != null) {
            progreso += 20;
        }

        // 5. Pasamos los datos al modelo para que la vista 'dashboard.html' los pinte.
        model.addAttribute("alumno", al);
        model.addAttribute("solicitudes", solicitudes);
        model.addAttribute("totalOfertas", totalOfertas);
        model.addAttribute("progresoPerfil", progreso);

        return "alumno/dashboard";
    }

    // Página completa donde nos aparecen todas las solicitudes realizadas por un
    // alumno.
    @GetMapping("/alumno/solicitudes")
    public String verSolicitudesAlumno(Model model) {
        Alumno al = alumnoRepository.findById(1L).orElse(null);

        if (al == null) {
            return "redirect:/ofertas?error=alumno-no-existe";
        }

        List<Solicitud> solicitudes = solicitudService.obtenerPorAlumno(al.getId());

        model.addAttribute("alumno", al);
        model.addAttribute("solicitudes", solicitudes);

        return "alumno/solicitudes";
    }

    @GetMapping("/alumno/perfil")
    public String verPerfil(Model model, Principal principal) {
        // 1. Obtenemos el email del usuario identificado.
        String emailLogueado = principal.getName();

        // 2. Buscamos al alumno por el email.
        Alumno alumno = alumnoService.buscarPorEmail(emailLogueado);

        // Verifica en la consola si realmente encuentra al alumno
        System.out.println("Datos: " + alumno.getUsuario().getNombre() + " " + alumno.getUsuario().getApellidos());

        // 3. Pasamos el objeto al modelo.
        model.addAttribute("alumno", alumno);

        return "alumno/perfil";
    }

    @PostMapping("/alumno/perfil/actualizar")
    @Transactional // Asegura que todo ocurra en una sola transacción
    public String actualizarPerfil(@Valid @ModelAttribute("alumno") Alumno alumnoForm, BindingResult result,
            Model model, Principal principal) {

        if (result.hasErrors()) {
            System.out.println("Errores detectados: " + result.getAllErrors());
            return "alumno/perfil";
        }

        // 1. Cargar el alumno real con su Usuario cargado
        Alumno alumnoDb = alumnoService.buscarPorEmail(principal.getName());

        // 2. Sincronizar datos de Usuario (IMPORTANTE)
        if (alumnoForm.getUsuario() != null) {
            alumnoDb.getUsuario().setNombre(alumnoForm.getUsuario().getNombre());
            alumnoDb.getUsuario().setApellidos(alumnoForm.getUsuario().getApellidos());
            if (alumnoForm.getUsuario().getFNac() != null) {
                alumnoDb.getUsuario().setFNac(alumnoForm.getUsuario().getFNac());
            }
        }

        // 3. Sincronizar datos de Alumno
        alumnoDb.setDni(alumnoForm.getDni());
        alumnoDb.setCiudad(alumnoForm.getCiudad());
        alumnoDb.setCentroEducativo(alumnoForm.getCentroEducativo());
        alumnoDb.setEstadoFct(alumnoForm.getEstadoFct());
        alumnoDb.setHorasFct(alumnoForm.getHorasFct());
        alumnoDb.setEmpresaFct(alumnoForm.getEmpresaFct());

        // 4. Guardar
        alumnoService.guardar(alumnoDb);

        // 5. Mensaje de éxito. 
        System.out.println("Ciudad recibida: " + alumnoForm.getCiudad());

        return "redirect:/alumno/perfil?exito";
    }

    @GetMapping("/alumno/config")
    public String mostrarConfiguracion(Model model, Principal principal) {
        String email = principal.getName();
        Alumno alumno = alumnoService.buscarPorEmail(email);
        model.addAttribute("alumno", alumno);
        return "alumno/config";
    }
}
