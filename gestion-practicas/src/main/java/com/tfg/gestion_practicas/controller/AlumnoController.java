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
    private final AlumnoService alumnoService;

    @Autowired
    private SolicitudService solicitudService;

    @Autowired
    private AlumnoRepository alumnoRepository;

    @Autowired
    private OfertaRepository ofertaRepository;

    AlumnoController(AlumnoService alumnoService) {
        this.alumnoService = alumnoService;
    }

    @GetMapping("/alumno/dashboard")
    public String dashboardAlumno(Model model, Principal principal) {
        if (principal == null) return "redirect:/login";

        String email = principal.getName();
        Alumno al = alumnoRepository.findByUsuarioCorreo(email).orElse(null);

        if (al == null) return "redirect:/login?error=usuario-no-encontrado";

        List<Solicitud> solicitudes = solicitudService.obtenerPorAlumno(al.getId());
        Long totalOfertas = ofertaRepository.count();

        int progreso = 0;
        if (al.getDni() != null && !al.getDni().isEmpty())               progreso += 20;
        if (al.getUsuario().getNombre() != null
                && !al.getUsuario().getNombre().isEmpty())               progreso += 20;
        if (al.getUsuario().getCorreo() != null
                && !al.getUsuario().getCorreo().isEmpty())               progreso += 20;
        if (al.getMatricula() != null)                                   progreso += 20;
        if (al.getCvUrl() != null)                                       progreso += 20;

        model.addAttribute("alumno", al);
        model.addAttribute("solicitudes", solicitudes);
        model.addAttribute("totalOfertas", totalOfertas);
        model.addAttribute("progresoPerfil", progreso);

        return "alumno/dashboard";
    }

    // ✅ CORRECCIÓN: antes usaba findById(1L) hardcodeado.
    // Ahora obtiene el alumno real a partir del usuario autenticado.
    @GetMapping("/alumno/solicitudes")
    public String verSolicitudesAlumno(Model model, Principal principal) {
        if (principal == null) return "redirect:/login";

        Alumno al = alumnoRepository.findByUsuarioCorreo(principal.getName()).orElse(null);

        if (al == null) return "redirect:/ofertas?error=alumno-no-existe";

        List<Solicitud> solicitudes = solicitudService.obtenerPorAlumno(al.getId());

        model.addAttribute("alumno", al);
        model.addAttribute("solicitudes", solicitudes);

        return "alumno/solicitudes";
    }

    @GetMapping("/alumno/perfil")
    public String verPerfil(Model model, Principal principal) {
        String emailLogueado = principal.getName();
        Alumno alumno = alumnoService.buscarPorEmail(emailLogueado);
        System.out.println("Datos: " + alumno.getUsuario().getNombre() + " " + alumno.getUsuario().getApellidos());
        model.addAttribute("alumno", alumno);
        return "alumno/perfil";
    }

    @PostMapping("/alumno/perfil/actualizar")
    @Transactional
    public String actualizarPerfil(@Valid @ModelAttribute("alumno") Alumno alumnoForm,
            BindingResult result, Model model, Principal principal) {

        if (result.hasErrors()) {
            System.out.println("Errores detectados: " + result.getAllErrors());
            return "alumno/perfil";
        }

        Alumno alumnoDb = alumnoService.buscarPorEmail(principal.getName());

        if (alumnoForm.getUsuario() != null) {
            alumnoDb.getUsuario().setNombre(alumnoForm.getUsuario().getNombre());
            alumnoDb.getUsuario().setApellidos(alumnoForm.getUsuario().getApellidos());
            if (alumnoForm.getUsuario().getFNac() != null) {
                alumnoDb.getUsuario().setFNac(alumnoForm.getUsuario().getFNac());
            }
        }

        alumnoDb.setDni(alumnoForm.getDni());
        alumnoDb.setCiudad(alumnoForm.getCiudad());
        alumnoDb.setCentroEducativo(alumnoForm.getCentroEducativo());
        alumnoDb.setEstadoFct(alumnoForm.getEstadoFct());
        alumnoDb.setHorasFct(alumnoForm.getHorasFct());
        alumnoDb.setEmpresaFct(alumnoForm.getEmpresaFct());

        alumnoService.guardar(alumnoDb);
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