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
         * Más adelante los conectamos con AlumnoRepository, TutorRepository y
         * EmpresaRepository.
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

    private boolean esAlumnoEnBusqueda(Alumno alumno) {
        if (alumno.getEstadoFct() == null) {
            return true;
        }

        String estado = alumno.getEstadoFct().trim();

        return estado.equalsIgnoreCase("PENDIENTE")
                || estado.equalsIgnoreCase("EN_BUSQUEDA")
                || estado.equalsIgnoreCase("EN BÚSQUEDA")
                || estado.equalsIgnoreCase("EN BUSQUEDA");
    }

    private boolean esAlumnoEnPracticas(Alumno alumno) {
        if (alumno.getEstadoFct() == null) {
            return false;
        }

        String estado = alumno.getEstadoFct().trim();

        return estado.equalsIgnoreCase("EN_PRACTICAS")
                || estado.equalsIgnoreCase("EN PRÁCTICAS")
                || estado.equalsIgnoreCase("EN PRACTICAS");
    }

    private boolean esAlumnoFinalizado(Alumno alumno) {
        if (alumno.getEstadoFct() == null) {
            return false;
        }

        return alumno.getEstadoFct().trim().equalsIgnoreCase("FINALIZADO");
    }

    private int calcularPorcentaje(long parte, long total) {
        if (total == 0) {
            return 0;
        }

        return (int) Math.round((parte * 100.0) / total);
    }

    private int calcularIndiceGestion(int porcentajePracticas, int porcentajeConTutor, int porcentajeConEmpresa) {
        double indice = (porcentajePracticas * 0.4)
                + (porcentajeConTutor * 0.35)
                + (porcentajeConEmpresa * 0.25);

        return (int) Math.round(indice);
    }

    private String obtenerEstadoCentro(int indiceGestion) {
        if (indiceGestion >= 80) {
            return "Excelente";
        }

        if (indiceGestion >= 60) {
            return "Buen avance";
        }

        if (indiceGestion >= 40) {
            return "Necesita seguimiento";
        }

        return "Prioridad alta";
    }

    private String obtenerRecomendacionPrincipal(long alumnosSinTutor,
            long alumnosEnBusqueda,
            long alumnosSinEmpresa,
            long totalAlumnos) {
        if (totalAlumnos == 0) {
            return "Todavía no hay alumnos registrados en este centro.";
        }

        if (alumnosSinTutor > 0) {
            return "Hay alumnos sin tutor asignado. Conviene revisar la página de asignaciones.";
        }

        if (alumnosEnBusqueda > 0) {
            return "Hay alumnos todavía en búsqueda. Sería útil revisar ofertas disponibles y empresas colaboradoras.";
        }

        if (alumnosSinEmpresa > 0) {
            return "Algunos alumnos aún no tienen empresa FCT asociada.";
        }

        return "El centro presenta una gestión FCT muy completa. Mantén el seguimiento periódico.";
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
                        alumnoRepository.countByTutorId(tutor.getId())))
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

        List<Alumno> alumnosSinTutor = alumnoRepository
                .findByCentroNombreIgnoreCaseAndTutorIsNull(tutorCentro.getNombreCentro());

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

    @GetMapping("/tutor_centro/alumnos")
    public String listarAlumnosCentro(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        TutorCentro tutorCentro = obtenerTutorCentroLogueado(principal);

        List<Alumno> alumnos = alumnoRepository.findByCentroNombreIgnoreCase(tutorCentro.getNombreCentro());

        List<Tutor> tutores = tutorRepository.findByCentroEducativoIgnoreCase(tutorCentro.getNombreCentro());

        long totalAlumnos = alumnos.size();

        long alumnosEnBusqueda = alumnos.stream().filter(a -> a.getEstadoFct() == null
                || a.getEstadoFct().equalsIgnoreCase("PENDIENTE") || a.getEstadoFct().equalsIgnoreCase("EN_BUSQUEDA"))
                .count();

        long alumnosEnPracticas = alumnos.stream()
                .filter(a -> a.getEstadoFct() == null && a.getEstadoFct().equalsIgnoreCase("EN_PRACTICAS")).count();

        long alumnosFinalizados = alumnos.stream()
                .filter(a -> a.getEstadoFct() != null
                        && a.getEstadoFct().equalsIgnoreCase("FINALIZADO"))
                .count();

        long alumnosSinTutor = alumnos.stream().filter(a -> a.getTutor() == null).count();

        model.addAttribute("tutorCentro", tutorCentro.getUsuario());
        model.addAttribute("datosTutorCentro", tutorCentro);
        model.addAttribute("nombreCentro", tutorCentro.getNombreCentro());

        model.addAttribute("alumnos", alumnos);
        model.addAttribute("tutores", tutores);

        model.addAttribute("totalAlumnos", totalAlumnos);
        model.addAttribute("alumnosEnBusqueda", alumnosEnBusqueda);
        model.addAttribute("alumnosEnPracticas", alumnosEnPracticas);
        model.addAttribute("alumnosFinalizados", alumnosFinalizados);
        model.addAttribute("alumnosSinTutor", alumnosSinTutor);

        return "tutor_centro/alumnos";
    }

    @PostMapping("/tutor_centro/alumnos/asignar-tutor")
    public String asignarTutorDesdeAlumnos(@RequestParam("alumnoId") Long alumnoId,
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

        if (alumno.getCentro() == null
                || alumno.getCentro().getNombre() == null
                || !alumno.getCentro().getNombre().equalsIgnoreCase(tutorCentro.getNombreCentro())) {
            redirectAttributes.addFlashAttribute("error", "No puedes asignar alumnos de otro centro.");
            return "redirect:/tutor_centro/alumnos";
        }

        if (tutor.getCentroEducativo() == null
                || !tutor.getCentroEducativo().equalsIgnoreCase(tutorCentro.getNombreCentro())) {
            redirectAttributes.addFlashAttribute("error", "No puedes asignar un tutor de otro centro.");
            return "redirect:/tutor_centro/alumnos";
        }

        alumno.setTutor(tutor);
        alumnoRepository.save(alumno);

        redirectAttributes.addFlashAttribute("success", "Tutor asignado correctamente al alumno.");

        return "redirect:/tutor_centro/alumnos";
    }

    @GetMapping("/tutor_centro/estadisticas")
    public String estadisticasTutorCentro(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        TutorCentro tutorCentro = obtenerTutorCentroLogueado(principal);

        List<Alumno> alumnos = alumnoRepository
                .findByCentroNombreIgnoreCase(tutorCentro.getNombreCentro());

        List<Tutor> tutores = tutorRepository
                .findByCentroEducativoIgnoreCase(tutorCentro.getNombreCentro());

        long totalAlumnos = alumnos.size();
        long totalTutores = tutores.size();

        long alumnosEnBusqueda = alumnos.stream()
                .filter(this::esAlumnoEnBusqueda)
                .count();

        long alumnosEnPracticas = alumnos.stream()
                .filter(this::esAlumnoEnPracticas)
                .count();

        long alumnosFinalizados = alumnos.stream()
                .filter(this::esAlumnoFinalizado)
                .count();

        long alumnosSinTutor = alumnos.stream()
                .filter(a -> a.getTutor() == null)
                .count();

        long alumnosConTutor = totalAlumnos - alumnosSinTutor;

        long alumnosConEmpresa = alumnos.stream()
                .filter(a -> a.getEmpresaFct() != null && !a.getEmpresaFct().isBlank())
                .count();

        long alumnosSinEmpresa = totalAlumnos - alumnosConEmpresa;

        List<TutorCentroTutorDTO> tutoresDTO = tutores.stream()
                .map(tutor -> new TutorCentroTutorDTO(
                        tutor,
                        alumnoRepository.countByTutorId(tutor.getId())))
                .toList();

        long tutoresConAlumnos = tutoresDTO.stream()
                .filter(t -> t.getAlumnosAsignados() > 0)
                .count();

        long tutoresSinAlumnos = totalTutores - tutoresConAlumnos;

        double mediaAlumnosPorTutor = totalTutores > 0
                ? (double) totalAlumnos / totalTutores
                : 0;

        int porcentajeEnBusqueda = calcularPorcentaje(alumnosEnBusqueda, totalAlumnos);
        int porcentajeEnPracticas = calcularPorcentaje(alumnosEnPracticas, totalAlumnos);
        int porcentajeFinalizados = calcularPorcentaje(alumnosFinalizados, totalAlumnos);
        int porcentajeConTutor = calcularPorcentaje(alumnosConTutor, totalAlumnos);
        int porcentajeConEmpresa = calcularPorcentaje(alumnosConEmpresa, totalAlumnos);

        int indiceGestion = calcularIndiceGestion(
                porcentajeEnPracticas,
                porcentajeConTutor,
                porcentajeConEmpresa);

        String estadoCentro = obtenerEstadoCentro(indiceGestion);

        String recomendacionPrincipal = obtenerRecomendacionPrincipal(
                alumnosSinTutor,
                alumnosEnBusqueda,
                alumnosSinEmpresa,
                totalAlumnos);

        model.addAttribute("tutorCentro", tutorCentro.getUsuario());
        model.addAttribute("datosTutorCentro", tutorCentro);
        model.addAttribute("nombreCentro", tutorCentro.getNombreCentro());

        model.addAttribute("totalAlumnos", totalAlumnos);
        model.addAttribute("totalTutores", totalTutores);

        model.addAttribute("alumnosEnBusqueda", alumnosEnBusqueda);
        model.addAttribute("alumnosEnPracticas", alumnosEnPracticas);
        model.addAttribute("alumnosFinalizados", alumnosFinalizados);

        model.addAttribute("alumnosSinTutor", alumnosSinTutor);
        model.addAttribute("alumnosConTutor", alumnosConTutor);

        model.addAttribute("alumnosConEmpresa", alumnosConEmpresa);
        model.addAttribute("alumnosSinEmpresa", alumnosSinEmpresa);

        model.addAttribute("tutoresConAlumnos", tutoresConAlumnos);
        model.addAttribute("tutoresSinAlumnos", tutoresSinAlumnos);
        model.addAttribute("mediaAlumnosPorTutor", mediaAlumnosPorTutor);

        model.addAttribute("porcentajeEnBusqueda", porcentajeEnBusqueda);
        model.addAttribute("porcentajeEnPracticas", porcentajeEnPracticas);
        model.addAttribute("porcentajeFinalizados", porcentajeFinalizados);
        model.addAttribute("porcentajeConTutor", porcentajeConTutor);
        model.addAttribute("porcentajeConEmpresa", porcentajeConEmpresa);

        model.addAttribute("tutoresDTO", tutoresDTO);

        model.addAttribute("indiceGestion", indiceGestion);
        model.addAttribute("estadoCentro", estadoCentro);
        model.addAttribute("recomendacionPrincipal", recomendacionPrincipal);

        return "tutor_centro/estadisticas";
    }
}