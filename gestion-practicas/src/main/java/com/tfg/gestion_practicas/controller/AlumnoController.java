package com.tfg.gestion_practicas.controller;

import com.tfg.gestion_practicas.services.AlumnoService;
import java.util.List;
import java.security.Principal;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.tfg.gestion_practicas.model.Alumno;
import com.tfg.gestion_practicas.model.EstadoSolicitud;
import com.tfg.gestion_practicas.model.Oferta;
import com.tfg.gestion_practicas.model.Solicitud;
import com.tfg.gestion_practicas.repository.AlumnoRepository;
import com.tfg.gestion_practicas.repository.OfertaRepository;
import com.tfg.gestion_practicas.repository.SolicitudRepository;
import com.tfg.gestion_practicas.services.SolicitudService;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@Controller
public class AlumnoController {
    private final AlumnoService alumnoService;

    @Autowired
    private SolicitudService solicitudService;

    @Autowired
    private SolicitudRepository solicitudRepository;

    @Autowired
    private AlumnoRepository alumnoRepository;

    @Autowired
    private OfertaRepository ofertaRepository;

    AlumnoController(AlumnoService alumnoService) {
        this.alumnoService = alumnoService;
    }

    @GetMapping("/alumno/dashboard")
    public String dashboardAlumno(Model model, Principal principal) {
        if (principal == null)
            return "redirect:/login";

        String email = principal.getName();
        Alumno al = alumnoRepository.findByUsuarioCorreo(email).orElse(null);

        if (al == null)
            return "redirect:/login?error=usuario-no-encontrado";

        List<Solicitud> solicitudes = solicitudService.obtenerPorAlumno(al.getId());
        Long totalOfertas = ofertaRepository.count();

        int progreso = 0;
        if (al.getDni() != null && !al.getDni().isEmpty())
            progreso += 20;
        if (al.getUsuario().getNombre() != null
                && !al.getUsuario().getNombre().isEmpty())
            progreso += 20;
        if (al.getUsuario().getCorreo() != null
                && !al.getUsuario().getCorreo().isEmpty())
            progreso += 20;
        if (al.getMatricula() != null)
            progreso += 20;
        if (al.getCvUrl() != null)
            progreso += 20;

        model.addAttribute("alumno", al);
        model.addAttribute("solicitudes", solicitudes);
        model.addAttribute("totalOfertas", totalOfertas);
        model.addAttribute("progresoPerfil", progreso);

        return "alumno/dashboard";
    }

    // ✅ CORRECCIÓN: antes usaba findById(1L) hardcodeado.
    // Ahora obtiene el alumno real a partir del usuario autenticado.
    @GetMapping("/alumno/solicitudes")
    public String listarMisSolicitudes(Model model, Principal principal) {
        // 1. Buscamos al alumno por el email del usuario autenticado
        Alumno alumno = alumnoRepository.findByUsuarioCorreo(principal.getName())
                .orElseThrow(() -> new RuntimeException("Alumno no encontrado"));

        // 2. Obtenemos sus solicitudes (necesitas este método en tu
        // SolicitudRepository)
        List<Solicitud> misSolicitudes = solicitudRepository.findByAlumno(alumno);

        // 3. Pasamos la lista a la vista
        model.addAttribute("solicitudes", misSolicitudes);

        return "alumno/solicitudes"; // Esta es la ruta del archivo que crearemos
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

    @GetMapping("/ofertas/detalle/{id}")
    public String detalleOferta(@PathVariable Long id, Model model, Principal principal) {
        if (principal == null)
            return "redirect:/login";

        Alumno al = alumnoRepository.findByUsuarioCorreo(principal.getName()).orElse(null);
        Oferta oferta = ofertaRepository.findById(id).orElse(null);

        if (oferta == null)
            return "redirect:/ofertas?error=no-encontrada";

        model.addAttribute("alumno", al);
        model.addAttribute("oferta", oferta);

        // Comprobar si el alumno ya ha solicitado esta oferta para deshabilitar el
        // botón
        boolean yaSolicitada = solicitudService.obtenerPorAlumno(al.getId())
                .stream()
                .anyMatch(s -> s.getOferta().getId().equals(id));

        model.addAttribute("yaSolicitada", yaSolicitada);

        return "alumno/detalle-oferta";
    }

    @PostMapping("/alumno/solicitar")
    @Transactional
    public String procesarSolicitud(@RequestParam("ofertaId") Long ofertaId,
            @RequestParam(value = "mensaje", defaultValue = "Solicitud enviada por el alumno a través de la plataforma.") String mensaje,
            Principal principal) {
        if (principal == null)
            return "redirect:/login";

        Alumno alumno = alumnoRepository.findByUsuarioCorreo(principal.getName())
                .orElseThrow(() -> new RuntimeException("Alumno no encontrado"));

        Oferta oferta = ofertaRepository.findById(ofertaId)
                .orElseThrow(() -> new RuntimeException("Oferta no encontrada"));

        // Evitar duplicados
        boolean yaExiste = solicitudService.obtenerPorAlumno(alumno.getId())
                .stream()
                .anyMatch(s -> s.getOferta().getId().equals(ofertaId));

        if (!yaExiste) {
            Solicitud nuevaSolicitud = Solicitud.builder()
                    .alumno(alumno)
                    .oferta(oferta)
                    .mensaje(mensaje) // Tu modelo requiere mensaje (min 10 caracteres)
                    .fechaSolicitud(LocalDate.now()) // Fecha actual
                    .estado(EstadoSolicitud.PENDIENTE) // Corregido según image_48070e.png
                    .build();

            solicitudService.guardar(nuevaSolicitud);
        }

        return "redirect:/alumno/solicitudes?exito";
    }

}