package com.tfg.gestion_practicas.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.tfg.gestion_practicas.model.Alumno;
import com.tfg.gestion_practicas.model.Solicitud;
import com.tfg.gestion_practicas.model.Tutor;
import com.tfg.gestion_practicas.repository.AlumnoRepository;
import com.tfg.gestion_practicas.repository.SolicitudRepository;
import com.tfg.gestion_practicas.repository.TutorRepository;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.tfg.gestion_practicas.model.ReporteTutor;
import com.tfg.gestion_practicas.repository.ReporteTutorRepository;

@Controller
public class TutorController {

    @Autowired
    private TutorRepository tutorRepository;

    @Autowired
    private AlumnoRepository alumnoRepository;

    @Autowired
    private SolicitudRepository solicitudRepository;

    @Autowired
    private ReporteTutorRepository reporteTutorRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @GetMapping("/tutor/dashboard")
    public String dashboardTutor(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        Tutor tutor = obtenerTutorLogueado(principal);

        List<Alumno> misAlumnos = alumnoRepository.findByTutorId(tutor.getId());
        List<Solicitud> solicitudesMisAlumnos = solicitudRepository.findByAlumnoTutorId(tutor.getId());

        long totalAlumnos = misAlumnos.size();

        long enPracticas = misAlumnos.stream()
                .filter(a -> "EN_PRACTICAS".equals(a.getEstadoFct()))
                .count();

        long pendientesRevision = totalAlumnos - enPracticas;
        long totalSolicitudes = solicitudesMisAlumnos.size();

        model.addAttribute("tutor", tutor.getUsuario());
        model.addAttribute("alumnos", misAlumnos);
        model.addAttribute("totalAlumnos", totalAlumnos);
        model.addAttribute("enPracticas", enPracticas);
        model.addAttribute("pendientesRevision", pendientesRevision);
        model.addAttribute("totalSolicitudes", totalSolicitudes);

        return "tutor/dashboard";
    }

    @GetMapping("/tutor/alumnos")
    public String listarAlumnos(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        Tutor tutor = obtenerTutorLogueado(principal);

        List<Alumno> misAlumnos = alumnoRepository.findByTutorId(tutor.getId());

        model.addAttribute("tutor", tutor.getUsuario());
        model.addAttribute("alumnos", misAlumnos);

        return "tutor/alumnos";
    }

    @GetMapping("/tutor/alumnos/{id}")
    public String detalleAlumno(@PathVariable Long id, Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        Tutor tutor = obtenerTutorLogueado(principal);

        Alumno alumno = alumnoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alumno no encontrado"));

        if (alumno.getTutor() == null || !alumno.getTutor().getId().equals(tutor.getId())) {
            return "redirect:/tutor/alumnos?error=no-autorizado";
        }

        List<Solicitud> solicitudes = solicitudRepository.findByAlumnoId(alumno.getId());

        long pendientes = solicitudes.stream()
                .filter(s -> s.getEstado() != null && "PENDIENTE".equals(s.getEstado().name()))
                .count();

        long aceptadas = solicitudes.stream()
                .filter(s -> s.getEstado() != null && "ACEPTADA".equals(s.getEstado().name()))
                .count();

        long rechazadas = solicitudes.stream()
                .filter(s -> s.getEstado() != null && "RECHAZADA".equals(s.getEstado().name()))
                .count();

        model.addAttribute("tutor", tutor.getUsuario());
        model.addAttribute("alumno", alumno);
        model.addAttribute("solicitudes", solicitudes);
        model.addAttribute("totalSolicitudes", solicitudes.size());
        model.addAttribute("pendientes", pendientes);
        model.addAttribute("aceptadas", aceptadas);
        model.addAttribute("rechazadas", rechazadas);

        return "tutor/detalle-alumno";
    }

    @GetMapping("/tutor/solicitudes")
    public String listarSolicitudes(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        Tutor tutor = obtenerTutorLogueado(principal);

        List<Solicitud> solicitudes = solicitudRepository.findByAlumnoTutorId(tutor.getId());

        long pendientes = solicitudes.stream()
                .filter(s -> s.getEstado() != null && "PENDIENTE".equals(s.getEstado().name()))
                .count();

        long aceptadas = solicitudes.stream()
                .filter(s -> s.getEstado() != null && "ACEPTADA".equals(s.getEstado().name()))
                .count();

        long rechazadas = solicitudes.stream()
                .filter(s -> s.getEstado() != null && "RECHAZADA".equals(s.getEstado().name()))
                .count();

        model.addAttribute("tutor", tutor.getUsuario());
        model.addAttribute("solicitudes", solicitudes);
        model.addAttribute("totalSolicitudes", solicitudes.size());
        model.addAttribute("pendientes", pendientes);
        model.addAttribute("aceptadas", aceptadas);
        model.addAttribute("rechazadas", rechazadas);

        return "tutor/solicitudes";
    }

    @GetMapping("/tutor/reportes")
    public String verReportes(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        Tutor tutor = obtenerTutorLogueado(principal);

        List<Alumno> misAlumnos = alumnoRepository.findByTutorId(tutor.getId());
        List<ReporteTutor> reportes = reporteTutorRepository.findByTutorIdOrderByFechaCreacionDesc(tutor.getId());

        long totalReportes = reportes.size();

        long reportesSeguimiento = reportes.stream()
                .filter(r -> "SEGUIMIENTO".equals(r.getTipo()))
                .count();

        long reportesIncidencia = reportes.stream()
                .filter(r -> "INCIDENCIA".equals(r.getTipo()))
                .count();

        long reportesRevision = reportes.stream()
                .filter(r -> "REVISION".equals(r.getTipo()))
                .count();

        model.addAttribute("tutor", tutor.getUsuario());
        model.addAttribute("alumnos", misAlumnos);
        model.addAttribute("reportes", reportes);
        model.addAttribute("totalReportes", totalReportes);
        model.addAttribute("reportesSeguimiento", reportesSeguimiento);
        model.addAttribute("reportesIncidencia", reportesIncidencia);
        model.addAttribute("reportesRevision", reportesRevision);

        return "tutor/reportes";
    }

    @GetMapping("/tutor/perfil")
    public String verPerfil(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        Tutor tutor = obtenerTutorLogueado(principal);

        model.addAttribute("tutor", tutor.getUsuario());
        model.addAttribute("datosTutor", tutor);

        return "tutor/perfil";
    }

    @GetMapping("/tutor/configuracion")
    public String verConfiguracion(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        Tutor tutor = obtenerTutorLogueado(principal);

        model.addAttribute("tutor", tutor.getUsuario());
        model.addAttribute("datosTutor", tutor);

        return "tutor/configuracion";
    }

    @PostMapping("/tutor/configuracion/password")
    public String cambiarPasswordTutor(@RequestParam("passwordActual") String passwordActual,
                                    @RequestParam("passwordNueva") String passwordNueva,
                                    @RequestParam("passwordConfirmacion") String passwordConfirmacion,
                                    Principal principal,
                                    RedirectAttributes redirectAttributes) {

        if (principal == null) {
            return "redirect:/login";
        }

        Tutor tutor = obtenerTutorLogueado(principal);

        if (!passwordEncoder.matches(passwordActual, tutor.getUsuario().getPwd())) {
            redirectAttributes.addFlashAttribute("error", "La contraseña actual no es correcta.");
            return "redirect:/tutor/configuracion";
        }

        if (!passwordNueva.equals(passwordConfirmacion)) {
            redirectAttributes.addFlashAttribute("error", "Las nuevas contraseñas no coinciden.");
            return "redirect:/tutor/configuracion";
        }

        if (passwordNueva.length() < 6) {
            redirectAttributes.addFlashAttribute("error", "La nueva contraseña debe tener al menos 6 caracteres.");
            return "redirect:/tutor/configuracion";
        }

        tutor.getUsuario().setPwd(passwordEncoder.encode(passwordNueva));
        tutorRepository.save(tutor);

        redirectAttributes.addFlashAttribute("success", "Contraseña actualizada correctamente.");

        return "redirect:/tutor/configuracion";
    }


    private Tutor obtenerTutorLogueado(Principal principal) {
        return tutorRepository.findByUsuarioCorreo(principal.getName())
                .orElseThrow(() -> new RuntimeException("Tutor no encontrado"));
    }

    @PostMapping("/tutor/reportes/crear")
    public String crearReporte(@RequestParam("alumnoId") Long alumnoId,
                            @RequestParam("tipo") String tipo,
                            @RequestParam("comentario") String comentario,
                            Principal principal,
                            RedirectAttributes redirectAttributes) {

        if (principal == null) {
            return "redirect:/login";
        }

        Tutor tutor = obtenerTutorLogueado(principal);

        Alumno alumno = alumnoRepository.findById(alumnoId)
                .orElseThrow(() -> new RuntimeException("Alumno no encontrado"));

        if (alumno.getTutor() == null || !alumno.getTutor().getId().equals(tutor.getId())) {
            redirectAttributes.addFlashAttribute("error", "No puedes crear reportes sobre alumnos que no tienes asignados.");
            return "redirect:/tutor/reportes";
        }

        if (comentario == null || comentario.isBlank()) {
            redirectAttributes.addFlashAttribute("error", "El comentario del reporte no puede estar vacío.");
            return "redirect:/tutor/reportes";
        }

        ReporteTutor reporte = new ReporteTutor();
        reporte.setTutor(tutor);
        reporte.setAlumno(alumno);
        reporte.setTipo(tipo);
        reporte.setComentario(comentario);

        reporteTutorRepository.save(reporte);

        redirectAttributes.addFlashAttribute("success", "Reporte creado correctamente.");

        return "redirect:/tutor/reportes";
    }

    @PostMapping("/tutor/perfil/actualizar")
    public String actualizarPerfilTutor(@RequestParam("especialidad") String especialidad,
                                        @RequestParam("telefono") String telefono,
                                        @RequestParam("departamento") String departamento,
                                        Principal principal,
                                        RedirectAttributes redirectAttributes) {

        if (principal == null) {
            return "redirect:/login";
        }

        Tutor tutor = obtenerTutorLogueado(principal);

        tutor.setEspecialidad(especialidad);
        tutor.setTelefono(telefono);
        tutor.setDepartamento(departamento);

        tutorRepository.save(tutor);

        redirectAttributes.addFlashAttribute("success", "Perfil actualizado correctamente.");

        return "redirect:/tutor/perfil";
    }

    @PostMapping("/tutor/configuracion/preferencias")
    public String actualizarPreferenciasTutor(@RequestParam(value = "notificacionesEmail", defaultValue = "false") Boolean notificacionesEmail,
                                            Principal principal,
                                            RedirectAttributes redirectAttributes) {

        if (principal == null) {
            return "redirect:/login";
        }

        Tutor tutor = obtenerTutorLogueado(principal);

        tutor.setNotificacionesEmail(notificacionesEmail);

        tutorRepository.save(tutor);

        redirectAttributes.addFlashAttribute("success", "Preferencias actualizadas correctamente.");

        return "redirect:/tutor/configuracion";
    }

    @PostMapping("/tutor/configuracion/desactivar")
    public String desactivarCuentaTutor(@RequestParam("confirmacion") String confirmacion,
                                        Principal principal,
                                        RedirectAttributes redirectAttributes) {

        if (principal == null) {
            return "redirect:/login";
        }

        if (!"DESACTIVAR".equals(confirmacion)) {
            redirectAttributes.addFlashAttribute("error", "Debes escribir DESACTIVAR para confirmar.");
            return "redirect:/tutor/configuracion";
        }

        Tutor tutor = obtenerTutorLogueado(principal);

        tutor.getUsuario().setActivo(false);

        tutorRepository.save(tutor);

        return "redirect:/logout";
    }
}