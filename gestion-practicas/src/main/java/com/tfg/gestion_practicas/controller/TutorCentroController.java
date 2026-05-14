package com.tfg.gestion_practicas.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.tfg.gestion_practicas.services.SupabaseStorageService;

import com.tfg.gestion_practicas.dto.TutorCentroTutorDTO;
import com.tfg.gestion_practicas.model.Alumno;
import com.tfg.gestion_practicas.model.Tutor;
import com.tfg.gestion_practicas.model.TutorCentro;
import com.tfg.gestion_practicas.repository.AlumnoRepository;
import com.tfg.gestion_practicas.repository.TutorCentroRepository;
import com.tfg.gestion_practicas.repository.TutorRepository;
import com.tfg.gestion_practicas.repository.UsuarioRepository;

@Controller
public class TutorCentroController {

    @Autowired
    private TutorCentroRepository tutorCentroRepository;

    @Autowired
    private TutorRepository tutorRepository;

    @Autowired
    private AlumnoRepository alumnoRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private SupabaseStorageService supabaseStorageService;

    @GetMapping("/tutor_centro/dashboard")
    public String dashboardTutorCentro(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        TutorCentro tutorCentro = obtenerTutorCentroLogueado(principal);
        String nombreCentro = obtenerNombreCentro(tutorCentro);

        List<Tutor> tutores = tutorRepository.findByCentroEducativoIgnoreCase(nombreCentro);
        List<Alumno> alumnos = alumnoRepository.findByCentroNombreIgnoreCase(nombreCentro);

        long totalTutores = tutores.size();
        long totalAlumnos = alumnos.size();

        long alumnosPracticas = alumnos.stream()
                .filter(this::esAlumnoEnPracticas)
                .count();

        long totalEmpresas = alumnos.stream()
                .filter(a -> a.getEmpresaFct() != null && !a.getEmpresaFct().isBlank())
                .map(a -> a.getEmpresaFct().trim().toLowerCase())
                .distinct()
                .count();

        long alumnosInformatica = alumnos.stream()
                .filter(this::esAlumnoInformatica)
                .count();

        long alumnosAdministracion = alumnos.stream()
                .filter(this::esAlumnoAdministracion)
                .count();

        long alumnosPeluqueria = alumnos.stream()
                .filter(this::esAlumnoPeluqueria)
                .count();

        long alumnosOtrasAreas = totalAlumnos - alumnosInformatica - alumnosAdministracion - alumnosPeluqueria;

        if (alumnosOtrasAreas < 0) {
            alumnosOtrasAreas = 0;
        }

        model.addAttribute("tutorCentro", tutorCentro.getUsuario());
        model.addAttribute("datosTutorCentro", tutorCentro);
        model.addAttribute("nombreCentro", nombreCentro);

        model.addAttribute("totalTutores", totalTutores);
        model.addAttribute("totalAlumnos", totalAlumnos);
        model.addAttribute("alumnosPracticas", alumnosPracticas);
        model.addAttribute("totalEmpresas", totalEmpresas);

        model.addAttribute("alumnosInformatica", alumnosInformatica);
        model.addAttribute("alumnosAdministracion", alumnosAdministracion);
        model.addAttribute("alumnosPeluqueria", alumnosPeluqueria);
        model.addAttribute("alumnosOtrasAreas", alumnosOtrasAreas);

        return "tutor_centro/dashboard";
    }

    @GetMapping("/tutor_centro/perfil")
    public String perfilTutorCentro(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        TutorCentro tutorCentro = obtenerTutorCentroLogueado(principal);
        String nombreCentro = obtenerNombreCentro(tutorCentro);

        model.addAttribute("tutorCentro", tutorCentro.getUsuario());
        model.addAttribute("datosTutorCentro", tutorCentro);
        model.addAttribute("nombreCentro", nombreCentro);

        return "tutor_centro/perfil";
    }

    @GetMapping("/tutor_centro/configuracion")
    public String configuracionTutorCentro(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        TutorCentro tutorCentro = obtenerTutorCentroLogueado(principal);
        String nombreCentro = obtenerNombreCentro(tutorCentro);

        model.addAttribute("tutorCentro", tutorCentro.getUsuario());
        model.addAttribute("datosTutorCentro", tutorCentro);
        model.addAttribute("nombreCentro", nombreCentro);

        return "tutor_centro/configuracion";
    }

    @GetMapping("/tutor_centro/tutores")
    public String listarTutoresCentro(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        TutorCentro tutorCentro = obtenerTutorCentroLogueado(principal);
        String nombreCentro = obtenerNombreCentro(tutorCentro);

        List<Tutor> tutores = tutorRepository.findByCentroEducativoIgnoreCase(nombreCentro);

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
        model.addAttribute("nombreCentro", nombreCentro);

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
        String nombreCentro = obtenerNombreCentro(tutorCentro);

        Tutor tutor = tutorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tutor no encontrado"));

        if (tutor.getCentroEducativo() == null ||
                !tutor.getCentroEducativo().equalsIgnoreCase(nombreCentro)) {
            return "redirect:/tutor_centro/tutores?error=no-autorizado";
        }

        List<Alumno> alumnos = alumnoRepository.findByTutorId(tutor.getId());

        long totalAlumnos = alumnos.size();

        model.addAttribute("tutorCentro", tutorCentro.getUsuario());
        model.addAttribute("datosTutorCentro", tutorCentro);
        model.addAttribute("nombreCentro", nombreCentro);

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
        String nombreCentro = obtenerNombreCentro(tutorCentro);

        List<Tutor> tutores = tutorRepository.findByCentroEducativoIgnoreCase(nombreCentro);

        List<Alumno> alumnosSinTutor = alumnoRepository
                .findByCentroNombreIgnoreCaseAndTutorIsNull(nombreCentro);

        model.addAttribute("tutorCentro", tutorCentro.getUsuario());
        model.addAttribute("datosTutorCentro", tutorCentro);
        model.addAttribute("nombreCentro", nombreCentro);

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
        String nombreCentro = obtenerNombreCentro(tutorCentro);

        Alumno alumno = alumnoRepository.findById(alumnoId)
                .orElseThrow(() -> new RuntimeException("Alumno no encontrado"));

        Tutor tutor = tutorRepository.findById(tutorId)
                .orElseThrow(() -> new RuntimeException("Tutor no encontrado"));

        if (alumno.getCentro() == null
                || alumno.getCentro().getNombre() == null
                || !alumno.getCentro().getNombre().equalsIgnoreCase(nombreCentro)) {
            redirectAttributes.addFlashAttribute("error", "No puedes asignar alumnos de otro centro.");
            return "redirect:/tutor_centro/asignaciones";
        }

        if (tutor.getCentroEducativo() == null
                || !tutor.getCentroEducativo().equalsIgnoreCase(nombreCentro)) {
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
        String nombreCentro = obtenerNombreCentro(tutorCentro);

        List<Alumno> alumnos = alumnoRepository.findByCentroNombreIgnoreCase(nombreCentro);
        List<Tutor> tutores = tutorRepository.findByCentroEducativoIgnoreCase(nombreCentro);

        long totalAlumnos = alumnos.size();

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

        model.addAttribute("tutorCentro", tutorCentro.getUsuario());
        model.addAttribute("datosTutorCentro", tutorCentro);
        model.addAttribute("nombreCentro", nombreCentro);

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
        String nombreCentro = obtenerNombreCentro(tutorCentro);

        Alumno alumno = alumnoRepository.findById(alumnoId)
                .orElseThrow(() -> new RuntimeException("Alumno no encontrado"));

        Tutor tutor = tutorRepository.findById(tutorId)
                .orElseThrow(() -> new RuntimeException("Tutor no encontrado"));

        if (alumno.getCentro() == null
                || alumno.getCentro().getNombre() == null
                || !alumno.getCentro().getNombre().equalsIgnoreCase(nombreCentro)) {
            redirectAttributes.addFlashAttribute("error", "No puedes asignar alumnos de otro centro.");
            return "redirect:/tutor_centro/alumnos";
        }

        if (tutor.getCentroEducativo() == null
                || !tutor.getCentroEducativo().equalsIgnoreCase(nombreCentro)) {
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
        String nombreCentro = obtenerNombreCentro(tutorCentro);

        List<Alumno> alumnos = alumnoRepository.findByCentroNombreIgnoreCase(nombreCentro);
        List<Tutor> tutores = tutorRepository.findByCentroEducativoIgnoreCase(nombreCentro);

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
        model.addAttribute("nombreCentro", nombreCentro);

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

    @PostMapping("/tutor_centro/perfil/actualizar")
    public String actualizarPerfilTutorCentro(@RequestParam("nombre") String nombre,
            @RequestParam("apellidos") String apellidos,
            @RequestParam("telefono") String telefono,
            Principal principal,
            RedirectAttributes redirectAttributes) {
        if (principal == null) {
            return "redirect:/login";
        }

        TutorCentro tutorCentro = obtenerTutorCentroLogueado(principal);

        tutorCentro.getUsuario().setNombre(nombre);
        tutorCentro.getUsuario().setApellidos(apellidos);
        tutorCentro.setTelefono(telefono);

        tutorCentroRepository.save(tutorCentro);

        redirectAttributes.addFlashAttribute("success", "Perfil actualizado correctamente.");

        return "redirect:/tutor_centro/perfil";
    }

    @PostMapping("/tutor_centro/configuracion/password")
    public String cambiarPasswordTutorCentro(@RequestParam("passwordActual") String passwordActual,
            @RequestParam("nuevaPassword") String nuevaPassword,
            @RequestParam("confirmarPassword") String confirmarPassword,
            Principal principal,
            RedirectAttributes redirectAttributes) {
        if (principal == null) {
            return "redirect:/login";
        }

        TutorCentro tutorCentro = obtenerTutorCentroLogueado(principal);

        if (!passwordEncoder.matches(passwordActual, tutorCentro.getUsuario().getPwd())) {
            redirectAttributes.addFlashAttribute("errorPassword", "La contraseña actual no es correcta.");
            return "redirect:/tutor_centro/configuracion";
        }

        if (nuevaPassword == null || nuevaPassword.length() < 6) {
            redirectAttributes.addFlashAttribute("errorPassword",
                    "La nueva contraseña debe tener al menos 6 caracteres.");
            return "redirect:/tutor_centro/configuracion";
        }

        if (!nuevaPassword.equals(confirmarPassword)) {
            redirectAttributes.addFlashAttribute("errorPassword", "Las contraseñas nuevas no coinciden.");
            return "redirect:/tutor_centro/configuracion";
        }

        tutorCentro.getUsuario().setPwd(passwordEncoder.encode(nuevaPassword));
        usuarioRepository.save(tutorCentro.getUsuario());

        redirectAttributes.addFlashAttribute("successPassword", "Contraseña actualizada correctamente.");

        return "redirect:/tutor_centro/configuracion";
    }

    @PostMapping("/tutor_centro/configuracion/desactivar")
    public String desactivarCuentaTutorCentro(@RequestParam("confirmacion") String confirmacion,
            Principal principal,
            RedirectAttributes redirectAttributes) {
        if (principal == null) {
            return "redirect:/login";
        }

        TutorCentro tutorCentro = obtenerTutorCentroLogueado(principal);

        if (!"DESACTIVAR".equals(confirmacion)) {
            redirectAttributes.addFlashAttribute("errorCuenta", "Debes escribir DESACTIVAR para confirmar la acción.");
            return "redirect:/tutor_centro/configuracion";
        }

        tutorCentro.getUsuario().setActivo(false);
        usuarioRepository.save(tutorCentro.getUsuario());

        return "redirect:/logout";
    }

    @GetMapping("/tutor_centro/alumnos/{id}/cv")
    public String verCvAlumnoTutorCentro(@PathVariable Long id,
            Principal principal,
            RedirectAttributes redirectAttributes) {
        try {
            if (principal == null) {
                return "redirect:/login";
            }

            TutorCentro tutorCentro = obtenerTutorCentroLogueado(principal);
            String nombreCentro = obtenerNombreCentro(tutorCentro);

            Alumno alumno = alumnoRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Alumno no encontrado"));

            if (alumno.getCentro() == null
                    || alumno.getCentro().getNombre() == null
                    || !alumno.getCentro().getNombre().equalsIgnoreCase(nombreCentro)) {
                redirectAttributes.addFlashAttribute("error", "No puedes acceder al CV de un alumno de otro centro.");
                return "redirect:/tutor_centro/alumnos";
            }

            if (alumno.getCvUrl() == null || alumno.getCvUrl().isBlank()) {
                redirectAttributes.addFlashAttribute("error", "Este alumno no tiene CV subido.");
                return "redirect:/tutor_centro/alumnos";
            }

            String urlFirmada = supabaseStorageService.crearUrlFirmada(alumno.getCvUrl());

            return "redirect:" + urlFirmada;

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "No se pudo abrir el CV del alumno.");
            return "redirect:/tutor_centro/alumnos";
        }
    }

    private TutorCentro obtenerTutorCentroLogueado(Principal principal) {
        return tutorCentroRepository.findByUsuarioCorreo(principal.getName())
                .orElseThrow(() -> new RuntimeException("Tutor de centro no encontrado"));
    }

    private String obtenerNombreCentro(TutorCentro tutorCentro) {
        if (tutorCentro.getCentro() == null || tutorCentro.getCentro().getNombre() == null) {
            throw new RuntimeException("El tutor de centro no tiene centro educativo asignado.");
        }

        return tutorCentro.getCentro().getNombre();
    }

    private boolean esAlumnoEnBusqueda(Alumno alumno) {
        if (alumno.getEstadoFct() == null) {
            return true;
        }

        String estado = alumno.getEstadoFct().trim();

        return estado.equalsIgnoreCase("PENDIENTE")
                || estado.equalsIgnoreCase("EN_BUSQUEDA")
                || estado.equalsIgnoreCase("EN BÚSQUEDA")
                || estado.equalsIgnoreCase("EN BUSQUEDA")
                || estado.equalsIgnoreCase("EN BÚSQUEDA");
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

    private boolean esAlumnoInformatica(Alumno alumno) {
        String texto = obtenerTextoClasificacionAlumno(alumno);

        return texto.contains("DAW")
                || texto.contains("DAM")
                || texto.contains("ASIR")
                || texto.contains("INFORMATICA")
                || texto.contains("INFORMÁTICA");
    }

    private boolean esAlumnoAdministracion(Alumno alumno) {
        String texto = obtenerTextoClasificacionAlumno(alumno);

        return texto.contains("ADMINISTRACION")
                || texto.contains("ADMINISTRACIÓN")
                || texto.contains("GESTION ADMINISTRATIVA")
                || texto.contains("GESTIÓN ADMINISTRATIVA");
    }

    private boolean esAlumnoPeluqueria(Alumno alumno) {
        String texto = obtenerTextoClasificacionAlumno(alumno);

        return texto.contains("PELUQUERÍA")
                || texto.contains("PELUQUERIA");
    }

    private String obtenerTextoClasificacionAlumno(Alumno alumno) {
        StringBuilder texto = new StringBuilder();

        if (alumno.getMatricula() != null) {
            texto.append(alumno.getMatricula()).append(" ");
        }

        if (alumno.getTutor() != null) {
            if (alumno.getTutor().getEspecialidad() != null) {
                texto.append(alumno.getTutor().getEspecialidad()).append(" ");
            }

            if (alumno.getTutor().getDepartamento() != null) {
                texto.append(alumno.getTutor().getDepartamento()).append(" ");
            }
        }

        return texto.toString().trim().toUpperCase();
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
}