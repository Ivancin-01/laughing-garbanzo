package com.tfg.gestion_practicas.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.tfg.gestion_practicas.model.TutorCentro;
import com.tfg.gestion_practicas.repository.TutorCentroRepository;
import com.tfg.gestion_practicas.model.Tutor;
import com.tfg.gestion_practicas.model.Alumno;
import com.tfg.gestion_practicas.repository.AlumnoRepository;
import com.tfg.gestion_practicas.repository.TutorRepository;

import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.tfg.gestion_practicas.dto.TutorCentroTutorDTO;


@Controller
public class TutorCentroController {

    @Autowired
    private TutorCentroRepository tutorCentroRepository;

    @Autowired
    private TutorRepository tutorRepository;

    @Autowired
    private AlumnoRepository alumnoRepository;

    @GetMapping("/tutor_centro/dashboard")
    public String dashboardTutorCentro(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        TutorCentro tutorCentro = obtenerTutorCentroLogueado(principal);

        model.addAttribute("tutorCentro", tutorCentro.getUsuario());
        model.addAttribute("nombreCentro", tutorCentro.getNombreCentro());
        model.addAttribute("tutorDocente", tutorCentro.getTutor());

        /*
         * Datos temporales para que el dashboard no rompa.
         * Más adelante los conectamos con AlumnoRepository, TutorRepository y EmpresaRepository.
         */
        model.addAttribute("totalTutores", 0);
        model.addAttribute("totalAlumnos", 0);
        model.addAttribute("alumnosPracticas", 0);
        model.addAttribute("totalEmpresas", 0);

        model.addAttribute("alumnosInformatica", 0);
        model.addAttribute("alumnosAdministracion", 0);
        model.addAttribute("alumnosOtrasAreas", 0);

        return "tutor_centro/dashboard";
    }

    @GetMapping("/tutor_centro/perfil")
    public String perfilTutorCentro(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        TutorCentro tutorCentro = obtenerTutorCentroLogueado(principal);

        model.addAttribute("tutorCentro", tutorCentro.getUsuario());
        model.addAttribute("datosTutorCentro", tutorCentro);

        return "tutor_centro/perfil";
    }

    @GetMapping("/tutor_centro/configuracion")
    public String configuracionTutorCentro(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        TutorCentro tutorCentro = obtenerTutorCentroLogueado(principal);

        model.addAttribute("tutorCentro", tutorCentro.getUsuario());
        model.addAttribute("datosTutorCentro", tutorCentro);

        return "tutor_centro/configuracion";
    }

    private TutorCentro obtenerTutorCentroLogueado(Principal principal) {
        return tutorCentroRepository.findByUsuarioCorreo(principal.getName())
                .orElseThrow(() -> new RuntimeException("Tutor de centro no encontrado"));
    }

    @GetMapping("/tutor_centro/tutores")
    public String listarTutoresCentro(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        TutorCentro tutorCentro = obtenerTutorCentroLogueado(principal);

        List<Tutor> tutores = tutorRepository
                .findByCentroEducativoIgnoreCase(tutorCentro.getNombreCentro());

        List<TutorCentroTutorDTO> tutoresDTO = tutores.stream()
                .map(tutor -> new TutorCentroTutorDTO(
                        tutor,
                        alumnoRepository.countByTutorId(tutor.getId())
                ))
                .toList();

        long totalTutores = tutoresDTO.size();

        long tutoresDisponibles = tutoresDTO.stream()
                .filter(TutorCentroTutorDTO::isDisponible)
                .count();

        long tutoresConAlumnos = tutoresDTO.stream()
                .filter(t -> t.getAlumnosAsignados() > 0)
                .count();

        long totalAlumnosAsignados = tutoresDTO.stream()
                .mapToLong(TutorCentroTutorDTO::getAlumnosAsignados)
                .sum();

        model.addAttribute("tutorCentro", tutorCentro.getUsuario());
        model.addAttribute("datosTutorCentro", tutorCentro);
        model.addAttribute("nombreCentro", tutorCentro.getNombreCentro());

        model.addAttribute("tutores", tutoresDTO);
        model.addAttribute("totalTutores", totalTutores);
        model.addAttribute("tutoresDisponibles", tutoresDisponibles);
        model.addAttribute("tutoresConAlumnos", tutoresConAlumnos);
        model.addAttribute("totalAlumnosAsignados", totalAlumnosAsignados);

        return "tutor_centro/tutores";
    }

    @GetMapping("/tutor_centro/tutores/{id}/alumnos")
    public String verAlumnosTutor(@PathVariable Long id, Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        TutorCentro tutorCentro = obtenerTutorCentroLogueado(principal);

        Tutor tutor = tutorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tutor no encontrado"));

                
        if (tutor.getCentroEducativo() == null ||
                !tutor.getCentroEducativo().equalsIgnoreCase(tutorCentro.getNombreCentro())) {
            return "redirect:/tutor_centro/tutores?error=no-autorizado";
        }

        List<Alumno> alumnos = alumnoRepository.findByTutorId(tutor.getId());

        long totalAlumnos = alumnos.size();

        model.addAttribute("tutorCentro", tutorCentro.getUsuario());
        model.addAttribute("datosTutorCentro", tutorCentro);

        model.addAttribute("tutor", tutor);
        model.addAttribute("alumnos", alumnos);
        model.addAttribute("totalAlumnos", totalAlumnos);

        return "tutor_centro/alumnos-tutor";
    }

    @GetMapping("/tutor_centro/asignaciones")
    public String verAsignaciones(@RequestParam(name = "tutorId", required = false) Long tutorId,
                                Model model,
                                Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        TutorCentro tutorCentro = obtenerTutorCentroLogueado(principal);

        List<Tutor> tutores = tutorRepository
                .findByCentroEducativoIgnoreCase(tutorCentro.getNombreCentro());

        List<Alumno> alumnosSinTutor = alumnoRepository.findByTutorIsNull();

        model.addAttribute("tutorCentro", tutorCentro.getUsuario());
        model.addAttribute("datosTutorCentro", tutorCentro);

        model.addAttribute("tutores", tutores);
        model.addAttribute("alumnosSinTutor", alumnosSinTutor);
        model.addAttribute("tutorSeleccionadoId", tutorId);

        return "tutor_centro/asignaciones";
    }

    @PostMapping("/tutor_centro/asignaciones/asignar")
    public String asignarAlumnoATutor(@RequestParam("alumnoId") Long alumnoId,
                                    @RequestParam("tutorId") Long tutorId,
                                    Principal principal,
                                    RedirectAttributes redirectAttributes) {
        if (principal == null) {
            return "redirect:/login";
        }

        TutorCentro tutorCentro = obtenerTutorCentroLogueado(principal);

        Alumno alumno = alumnoRepository.findById(alumnoId)
                .orElseThrow(() -> new RuntimeException("Alumno no encontrado"));

        Tutor tutor = tutorRepository.findById(tutorId)
                .orElseThrow(() -> new RuntimeException("Tutor no encontrado"));

        /*
        * Seguridad:
        * Evitamos asignar alumnos a tutores de otro centro.
        */
        if (tutor.getCentroEducativo() == null ||
                !tutor.getCentroEducativo().equalsIgnoreCase(tutorCentro.getNombreCentro())) {
            redirectAttributes.addFlashAttribute("error", "No puedes asignar alumnos a un tutor de otro centro.");
            return "redirect:/tutor_centro/asignaciones";
        }

        alumno.setTutor(tutor);
        alumnoRepository.save(alumno);

        redirectAttributes.addFlashAttribute("success", "Alumno asignado correctamente.");

        return "redirect:/tutor_centro/asignaciones?tutorId=" + tutor.getId();
    }
}