package com.tfg.gestion_practicas.controller;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.tfg.gestion_practicas.model.Alumno;
import com.tfg.gestion_practicas.model.Empresa;
import com.tfg.gestion_practicas.model.EstadoPractica;
import com.tfg.gestion_practicas.model.EstadoSolicitud;
import com.tfg.gestion_practicas.model.Oferta;
import com.tfg.gestion_practicas.model.Solicitud;
import com.tfg.gestion_practicas.repository.AlumnoRepository;
import com.tfg.gestion_practicas.repository.EmpresaRepository;
import com.tfg.gestion_practicas.repository.OfertaRepository;
import com.tfg.gestion_practicas.repository.SolicitudRepository;
import com.tfg.gestion_practicas.services.EmpresaService;

import com.tfg.gestion_practicas.services.SupabaseStorageService;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class EmpresaController {

    @Autowired
    private EmpresaRepository empresaRepository;

    @Autowired
    private OfertaRepository ofertaRepository;

    @Autowired
    private SolicitudRepository solicitudRepository;

    @Autowired
    private EmpresaService empresaService;

    @Autowired
    private AlumnoRepository alumnoRepository;

    @Autowired
    private SupabaseStorageService supabaseStorageService;

    @GetMapping("/empresa/dashboard")
    public String dashboardEmpresa(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        String email = principal.getName();
        Empresa empresa = empresaRepository.findByUsuarioCorreo(email).orElse(null);

        if (empresa == null) {
            return "redirect:/login?error=empresa-no-encontrada";
        }

        List<Oferta> ofertasActivas = ofertaRepository.findByEmpresaId(empresa.getId());
        List<Solicitud> todasSolicitudes = solicitudRepository.findByOfertaEmpresaId(empresa.getId());

        List<Solicitud> solicitudesPendientes = todasSolicitudes.stream()
                .filter(s -> s.getEstado() == EstadoSolicitud.PENDIENTE)
                .collect(Collectors.toList());

        long totalAlumnosFct = todasSolicitudes.stream()
                .filter(s -> s.getEstado() == EstadoSolicitud.ACEPTADA)
                .count();

        model.addAttribute("empresa", empresa);
        model.addAttribute("ofertasActivas", ofertasActivas);
        model.addAttribute("solicitudesPendientes", solicitudesPendientes);
        model.addAttribute("totalAlumnosFct", totalAlumnosFct);

        return "empresa/dashboard";
    }

    @GetMapping("/empresa/crear-oferta")
    public String mostrarFormularioCrearOferta(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        String email = principal.getName();
        Empresa empresa = empresaRepository.findByUsuarioCorreo(email).orElse(null);

        if (empresa == null) {
            return "redirect:/login";
        }

        model.addAttribute("empresa", empresa);
        model.addAttribute("oferta", new Oferta());

        return "empresa/crear-oferta";
    }

    @PostMapping("/empresa/ofertas/guardar")
    public String guardarOferta(@ModelAttribute("oferta") Oferta oferta, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        String email = principal.getName();
        Empresa empresa = empresaRepository.findByUsuarioCorreo(email).orElse(null);

        if (empresa != null) {
            oferta.setEmpresa(empresa);
            oferta.setFechaPublicacion(LocalDate.now());

            if (oferta.getCiudad() == null || oferta.getCiudad().isEmpty()) {
                oferta.setCiudad(empresa.getCiudad());
            }

            ofertaRepository.save(oferta);
        }

        return "redirect:/empresa/dashboard?creada=true";
    }

    @GetMapping("/empresa/perfil")
    public String mostrarPerfil(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        String email = principal.getName();
        Empresa empresa = empresaRepository.findByUsuarioCorreo(email).orElse(null);

        if (empresa == null) {
            return "redirect:/login";
        }

        List<Oferta> ofertasActivas = ofertaRepository.findByEmpresaId(empresa.getId());

        model.addAttribute("empresa", empresa);
        model.addAttribute("ofertasActivas", ofertasActivas);

        return "empresa/perfil";
    }

    @PostMapping("/empresa/perfil/actualizar")
    public String actualizarPerfil(@ModelAttribute("empresa") Empresa empresaEditada, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        Empresa empresaOriginal = empresaRepository.findById(empresaEditada.getId()).orElse(null);

        if (empresaOriginal != null) {
            empresaOriginal.setNombre(empresaEditada.getNombre());
            empresaOriginal.setCif(empresaEditada.getCif());
            empresaOriginal.setTelefono(empresaEditada.getTelefono());
            empresaOriginal.setCiudad(empresaEditada.getCiudad());
            empresaOriginal.setSector(empresaEditada.getSector());
            empresaOriginal.setWeb(empresaEditada.getWeb());
            empresaOriginal.setDescripcion(empresaEditada.getDescripcion());

            empresaRepository.save(empresaOriginal);
        }

        return "redirect:/empresa/perfil?exito=true";
    }

    @GetMapping("/empresa/ofertas")
    public String listarOfertas(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        String email = principal.getName();
        Empresa empresa = empresaRepository.findByUsuarioCorreo(email).orElse(null);

        if (empresa == null) {
            return "redirect:/login";
        }

        List<Oferta> ofertas = ofertaRepository.findByEmpresaId(empresa.getId());

        model.addAttribute("empresa", empresa);
        model.addAttribute("ofertas", ofertas);

        return "empresa/ofertas";
    }

    @GetMapping("/empresa/ofertas/detalles/{id}")
    public String verDetallesOferta(@PathVariable Long id, Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        String email = principal.getName();
        Empresa empresa = empresaRepository.findByUsuarioCorreo(email).orElse(null);

        if (empresa == null) {
            return "redirect:/login";
        }

        Oferta oferta = ofertaRepository.findById(id).orElse(null);

        if (oferta == null || !oferta.getEmpresa().getId().equals(empresa.getId())) {
            return "redirect:/empresa/ofertas?error=no-autorizado";
        }

        List<Solicitud> solicitudesOferta = solicitudRepository.findByOfertaId(id);

        long pendientes = solicitudesOferta.stream()
                .filter(s -> s.getEstado() == EstadoSolicitud.PENDIENTE)
                .count();

        long aceptadas = solicitudesOferta.stream()
                .filter(s -> s.getEstado() == EstadoSolicitud.ACEPTADA)
                .count();

        long rechazadas = solicitudesOferta.stream()
                .filter(s -> s.getEstado() == EstadoSolicitud.RECHAZADA)
                .count();

        model.addAttribute("empresa", empresa);
        model.addAttribute("oferta", oferta);
        model.addAttribute("solicitudesOferta", solicitudesOferta);
        model.addAttribute("pendientes", pendientes);
        model.addAttribute("aceptadas", aceptadas);
        model.addAttribute("rechazadas", rechazadas);

        return "empresa/detalles-oferta";
    }

    @PostMapping("/empresa/ofertas/eliminar/{id}")
    public String eliminarOferta(@PathVariable Long id, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        String email = principal.getName();
        Oferta oferta = ofertaRepository.findById(id).orElse(null);

        if (oferta == null) {
            return "redirect:/empresa/ofertas?error=oferta-no-encontrada";
        }

        if (!oferta.getEmpresa().getUsuario().getCorreo().equals(email)) {
            return "redirect:/empresa/ofertas?error=no-autorizado";
        }

        List<Solicitud> solicitudes = solicitudRepository.findByOfertaId(id);

        if (!solicitudes.isEmpty()) {
            return "redirect:/empresa/ofertas?error=oferta-con-solicitudes";
        }

        ofertaRepository.delete(oferta);

        return "redirect:/empresa/ofertas?eliminada=true";
    }

    @GetMapping("/empresa/ofertas/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        String email = principal.getName();
        Empresa empresa = empresaRepository.findByUsuarioCorreo(email).orElse(null);
        Oferta oferta = ofertaRepository.findById(id).orElse(null);

        if (empresa == null) {
            return "redirect:/login";
        }

        if (oferta == null || !oferta.getEmpresa().getId().equals(empresa.getId())) {
            return "redirect:/empresa/ofertas?error=no-autorizado";
        }

        model.addAttribute("empresa", empresa);
        model.addAttribute("oferta", oferta);

        return "empresa/editar-oferta";
    }

    @PostMapping("/empresa/ofertas/actualizar")
    public String actualizarOferta(@ModelAttribute("oferta") Oferta ofertaEditada, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        Oferta ofertaOriginal = ofertaRepository.findById(ofertaEditada.getId()).orElse(null);

        if (ofertaOriginal != null) {
            ofertaOriginal.setTitulo(ofertaEditada.getTitulo());
            ofertaOriginal.setDescripcion(ofertaEditada.getDescripcion());
            ofertaOriginal.setModalidad(ofertaEditada.getModalidad());
            ofertaOriginal.setHorario(ofertaEditada.getHorario());
            ofertaOriginal.setPlazas(ofertaEditada.getPlazas());
            ofertaOriginal.setCiudad(ofertaEditada.getCiudad());
            ofertaOriginal.setEspecialidad(ofertaEditada.getEspecialidad());

            ofertaRepository.save(ofertaOriginal);
        }

        return "redirect:/empresa/ofertas?editada=true";
    }

    @GetMapping("/empresa/solicitudes")
    public String listarSolicitudes(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        String email = principal.getName();
        Empresa empresa = empresaRepository.findByUsuarioCorreo(email).orElse(null);

        if (empresa == null) {
            return "redirect:/login";
        }

        List<Solicitud> solicitudes = solicitudRepository.findByOfertaEmpresaId(empresa.getId());

        model.addAttribute("empresa", empresa);
        model.addAttribute("solicitudes", solicitudes);

        return "empresa/solicitudes";
    }

    // =====================================================
    // GESTIÓN DE SOLICITUDES
    // =====================================================

    @PostMapping("/empresa/solicitudes/gestionar")
    @Transactional
    public String gestionarSolicitud(
            @RequestParam("solicitudId") Long solicitudId,
            @RequestParam("nuevoEstado") String nuevoEstado,
            @RequestParam(value = "origen", defaultValue = "solicitudes") String origen,
            Principal principal) {

        if (principal == null) {
            return "redirect:/login";
        }

        Empresa empresa = empresaRepository.findByUsuarioCorreo(principal.getName()).orElse(null);

        if (empresa == null) {
            return "redirect:/login";
        }

        Solicitud solicitud = solicitudRepository.findById(solicitudId).orElse(null);

        if (solicitud == null) {
            return redirigirGestionSolicitud(origen, "error", "solicitud_no_encontrada");
        }

        if (solicitud.getOferta() == null || solicitud.getOferta().getEmpresa() == null) {
            return redirigirGestionSolicitud(origen, "error", "oferta_no_valida");
        }

        if (!solicitud.getOferta().getEmpresa().getId().equals(empresa.getId())) {
            return redirigirGestionSolicitud(origen, "error", "no_autorizado");
        }

        if (solicitud.getEstado() != EstadoSolicitud.PENDIENTE) {
            return redirigirGestionSolicitud(origen, "error", "solicitud_ya_procesada");
        }

        try {
            EstadoSolicitud estadoConvertido = EstadoSolicitud.valueOf(nuevoEstado.toUpperCase());

            if (estadoConvertido == EstadoSolicitud.ACEPTADA) {
                solicitud.setEstado(EstadoSolicitud.ACEPTADA);
                solicitud.setEstadoPractica(EstadoPractica.PENDIENTE_INICIO);
            } else if (estadoConvertido == EstadoSolicitud.RECHAZADA) {
                solicitud.setEstado(EstadoSolicitud.RECHAZADA);
                solicitud.setEstadoPractica(null);
            } else {
                return redirigirGestionSolicitud(origen, "error", "estado_no_valido");
            }

            solicitudRepository.save(solicitud);

            if (solicitud.getAlumno() != null) {
                sincronizarEstadoFctAlumno(solicitud.getAlumno());
            }

            return redirigirGestionSolicitud(origen, "exito", "estado_actualizado");

        } catch (IllegalArgumentException e) {
            return redirigirGestionSolicitud(origen, "error", "estado_no_valido");
        }
    }

    /*
     * private String aceptarSolicitudEmpresa(Solicitud solicitud, String origen) {
     * Oferta oferta = solicitud.getOferta();
     * Alumno alumno = solicitud.getAlumno();
     * 
     * if (oferta == null) {
     * return redirigirGestionSolicitud(origen, "error", "oferta_no_valida");
     * }
     * 
     * if (alumno == null) {
     * return redirigirGestionSolicitud(origen, "error", "alumno_no_encontrado");
     * }
     * 
     * if (alumnoTienePracticaActivaPorSolicitudes(alumno)) {
     * return redirigirGestionSolicitud(origen, "error", "alumno_ya_en_practicas");
     * }
     * 
     * long aceptadasActuales = solicitudRepository.countByOfertaIdAndEstado(
     * oferta.getId(),
     * EstadoSolicitud.ACEPTADA);
     * 
     * if (oferta.getPlazas() != null && aceptadasActuales >= oferta.getPlazas()) {
     * return redirigirGestionSolicitud(origen, "error", "plazas_completas");
     * }
     * 
     * solicitud.setEstado(EstadoSolicitud.ACEPTADA);
     * solicitud.setEstadoPractica(EstadoPractica.PENDIENTE_INICIO);
     * solicitudRepository.save(solicitud);
     * 
     * sincronizarEstadoFctAlumno(alumno);
     * 
     * List<Solicitud> pendientesDelAlumno =
     * solicitudRepository.findByAlumnoIdAndEstado(
     * alumno.getId(),
     * EstadoSolicitud.PENDIENTE);
     * 
     * for (Solicitud s : pendientesDelAlumno) {
     * if (!s.getId().equals(solicitud.getId())) {
     * s.setEstado(EstadoSolicitud.RECHAZADA);
     * s.setEstadoPractica(null);
     * }
     * }
     * 
     * solicitudRepository.saveAll(pendientesDelAlumno);
     * 
     * long aceptadasDespues = solicitudRepository.countByOfertaIdAndEstado(
     * oferta.getId(),
     * EstadoSolicitud.ACEPTADA);
     * 
     * if (oferta.getPlazas() != null && aceptadasDespues >= oferta.getPlazas()) {
     * List<Solicitud> pendientesMismaOferta =
     * solicitudRepository.findByOfertaIdAndEstado(
     * oferta.getId(),
     * EstadoSolicitud.PENDIENTE);
     * 
     * for (Solicitud s : pendientesMismaOferta) {
     * if (!s.getId().equals(solicitud.getId())) {
     * s.setEstado(EstadoSolicitud.RECHAZADA);
     * s.setEstadoPractica(null);
     * }
     * }
     * 
     * solicitudRepository.saveAll(pendientesMismaOferta);
     * 
     * }
     * 
     * return redirigirGestionSolicitud(origen, "exito", "estado_actualizado");
     * }
     * 
     * private String rechazarSolicitudEmpresa(Solicitud solicitud, String origen) {
     * Alumno alumno = solicitud.getAlumno();
     * 
     * solicitud.setEstado(EstadoSolicitud.RECHAZADA);
     * solicitud.setEstadoPractica(null);
     * solicitudRepository.save(solicitud);
     * 
     * if (alumno != null) {
     * sincronizarEstadoFctAlumno(alumno);
     * }
     * 
     * return redirigirGestionSolicitud(origen, "exito", "estado_actualizado");
     * }
     */

    private String redirigirGestionSolicitud(String origen, String tipo, String codigo) {
        String destino = "dashboard".equalsIgnoreCase(origen)
                ? "/empresa/dashboard"
                : "/empresa/solicitudes";

        return "redirect:" + destino + "?" + tipo + "=" + codigo;
    }

    // =====================================================
    // ALUMNOS EN PRÁCTICAS
    // =====================================================

    @GetMapping("/empresa/alumnos-fct")
    public String listarAlumnosEnPracticas(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        String email = principal.getName();
        Empresa empresa = empresaRepository.findByUsuarioCorreo(email).orElse(null);

        if (empresa == null) {
            return "redirect:/login";
        }

        List<Solicitud> alumnosPracticas = solicitudRepository.findByOfertaEmpresaIdAndEstado(
                empresa.getId(),
                EstadoSolicitud.ACEPTADA);

        model.addAttribute("empresa", empresa);
        model.addAttribute("alumnosPracticas", alumnosPracticas);

        return "empresa/alumnos-fct";
    }

    @PostMapping("/empresa/alumnos-fct/estado")
    @Transactional
    public String actualizarEstadoPractica(@RequestParam("solicitudId") Long solicitudId,
            @RequestParam("estadoPractica") EstadoPractica estadoPractica,
            Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        String email = principal.getName();
        Empresa empresa = empresaRepository.findByUsuarioCorreo(email).orElse(null);

        if (empresa == null) {
            return "redirect:/login";
        }

        Solicitud solicitud = solicitudRepository.findById(solicitudId).orElse(null);

        if (solicitud == null) {
            return "redirect:/empresa/alumnos-fct?errorEstado=true";
        }

        if (solicitud.getOferta() == null
                || solicitud.getOferta().getEmpresa() == null
                || !solicitud.getOferta().getEmpresa().getId().equals(empresa.getId())) {
            return "redirect:/empresa/alumnos-fct?errorEstado=true";
        }

        if (solicitud.getEstado() != EstadoSolicitud.ACEPTADA) {
            return "redirect:/empresa/alumnos-fct?errorEstado=true";
        }

        solicitud.setEstadoPractica(estadoPractica);
        solicitudRepository.save(solicitud);

        if (solicitud.getAlumno() != null) {
            sincronizarEstadoFctAlumno(solicitud.getAlumno());
        }

        return "redirect:/empresa/alumnos-fct?estadoActualizado=true";
    }

    // =====================================================
    // CONFIGURACIÓN EMPRESA
    // =====================================================

    @GetMapping("/empresa/configuracion")
    public String mostrarConfiguracion(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        String email = principal.getName();
        Empresa empresa = empresaRepository.findByUsuarioCorreo(email).orElse(null);

        if (empresa == null) {
            return "redirect:/login";
        }

        model.addAttribute("empresa", empresa);

        return "empresa/configuracion";
    }

    @PostMapping("/empresa/configuracion/password")
    public String cambiarPasswordEmpresa(@RequestParam("passwordActual") String passwordActual,
            @RequestParam("passwordNueva") String passwordNueva,
            @RequestParam("passwordConfirmacion") String passwordConfirmacion,
            Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        boolean actualizada = empresaService.cambiarPassword(
                principal.getName(),
                passwordActual,
                passwordNueva,
                passwordConfirmacion);

        if (!actualizada) {
            return "redirect:/empresa/configuracion?errorPassword=true";
        }

        return "redirect:/empresa/configuracion?passwordActualizada=true";
    }

    @PostMapping("/empresa/configuracion/contacto")
    public String actualizarContactoEmpresa(@RequestParam("emailContacto") String emailContacto,
            @RequestParam("telefonoContacto") String telefonoContacto,
            Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        boolean actualizado = empresaService.actualizarContacto(
                principal.getName(),
                emailContacto,
                telefonoContacto);

        if (!actualizado) {
            return "redirect:/empresa/configuracion?errorContacto=true";
        }

        return "redirect:/empresa/configuracion?datosActualizados=true";
    }

    @PostMapping("/empresa/configuracion/notificaciones")
    public String actualizarNotificacionesEmpresa(
            @RequestParam(value = "notificarSolicitudes", required = false) String notificarSolicitudes,
            @RequestParam(value = "resumenSemanal", required = false) String resumenSemanal,
            Principal principal) {

        if (principal == null) {
            return "redirect:/login";
        }

        boolean notificarSolicitudesActivo = notificarSolicitudes != null;
        boolean resumenSemanalActivo = resumenSemanal != null;

        boolean actualizado = empresaService.actualizarNotificaciones(
                principal.getName(),
                notificarSolicitudesActivo,
                resumenSemanalActivo);

        if (!actualizado) {
            return "redirect:/empresa/configuracion?errorNotificaciones=true";
        }

        return "redirect:/empresa/configuracion?notificacionesActualizadas=true";
    }

    // =====================================================
    // MÉTODOS PRIVADOS DE SINCRONIZACIÓN
    // =====================================================

    private boolean alumnoTienePracticaActivaPorSolicitudes(Alumno alumno) {
        return solicitudRepository.existsByAlumnoIdAndEstadoAndEstadoPracticaIn(
                alumno.getId(),
                EstadoSolicitud.ACEPTADA,
                List.of(
                        EstadoPractica.PENDIENTE_INICIO,
                        EstadoPractica.EN_PRACTICAS));
    }

    private void sincronizarEstadoFctAlumno(Alumno alumno) {
        List<Solicitud> solicitudesAceptadas = solicitudRepository.findByAlumnoIdAndEstado(
                alumno.getId(),
                EstadoSolicitud.ACEPTADA);

        boolean tienePracticaActiva = solicitudesAceptadas.stream()
                .anyMatch(s -> s.getEstadoPractica() == EstadoPractica.PENDIENTE_INICIO
                        || s.getEstadoPractica() == EstadoPractica.EN_PRACTICAS);

        boolean tienePracticaFinalizada = solicitudesAceptadas.stream()
                .anyMatch(s -> s.getEstadoPractica() == EstadoPractica.FINALIZADA);

        if (tienePracticaActiva) {
            alumno.setEstadoFct("EN_PRACTICAS");

            Solicitud solicitudActiva = solicitudesAceptadas.stream()
                    .filter(s -> s.getEstadoPractica() == EstadoPractica.PENDIENTE_INICIO
                            || s.getEstadoPractica() == EstadoPractica.EN_PRACTICAS)
                    .findFirst()
                    .orElse(null);

            if (solicitudActiva != null
                    && solicitudActiva.getOferta() != null
                    && solicitudActiva.getOferta().getEmpresa() != null) {
                alumno.setEmpresaFct(solicitudActiva.getOferta().getEmpresa().getNombre());
            }

        } else if (tienePracticaFinalizada) {
            alumno.setEstadoFct("FINALIZADO");

            Solicitud solicitudFinalizada = solicitudesAceptadas.stream()
                    .filter(s -> s.getEstadoPractica() == EstadoPractica.FINALIZADA)
                    .findFirst()
                    .orElse(null);

            if (solicitudFinalizada != null
                    && solicitudFinalizada.getOferta() != null
                    && solicitudFinalizada.getOferta().getEmpresa() != null) {
                alumno.setEmpresaFct(solicitudFinalizada.getOferta().getEmpresa().getNombre());
            }

        } else {
            alumno.setEstadoFct("EN_BUSQUEDA");
            alumno.setEmpresaFct(null);
        }

        alumnoRepository.save(alumno);
    }

    @GetMapping("/empresa/solicitudes/{id}/cv")
    public String verCvSolicitudEmpresa(@PathVariable Long id,
            Principal principal,
            RedirectAttributes redirectAttributes) {
        try {
            if (principal == null) {
                return "redirect:/login";
            }

            String email = principal.getName();
            Empresa empresa = empresaRepository.findByUsuarioCorreo(email).orElse(null);

            if (empresa == null) {
                return "redirect:/login";
            }

            Solicitud solicitud = solicitudRepository.findById(id).orElse(null);

            if (solicitud == null) {
                redirectAttributes.addFlashAttribute("error", "Solicitud no encontrada.");
                return "redirect:/empresa/solicitudes";
            }

            if (solicitud.getOferta() == null
                    || solicitud.getOferta().getEmpresa() == null
                    || !solicitud.getOferta().getEmpresa().getId().equals(empresa.getId())) {
                redirectAttributes.addFlashAttribute("error", "No tienes permiso para ver este CV.");
                return "redirect:/empresa/solicitudes";
            }

            if (solicitud.getAlumno() == null
                    || solicitud.getAlumno().getCvUrl() == null
                    || solicitud.getAlumno().getCvUrl().isBlank()) {
                redirectAttributes.addFlashAttribute("error", "Este alumno no tiene CV subido.");
                return "redirect:/empresa/solicitudes";
            }

            String urlFirmada = supabaseStorageService.crearUrlFirmada(solicitud.getAlumno().getCvUrl());

            return "redirect:" + urlFirmada;

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "No se pudo abrir el CV del alumno.");
            return "redirect:/empresa/solicitudes";
        }
    }

    @GetMapping("/empresa/alumnos-fct/{id}/cv")
    public String verCvAlumnoFctEmpresa(@PathVariable Long id,
            Principal principal,
            RedirectAttributes redirectAttributes) {
        try {
            if (principal == null) {
                return "redirect:/login";
            }

            String email = principal.getName();
            Empresa empresa = empresaRepository.findByUsuarioCorreo(email).orElse(null);

            if (empresa == null) {
                return "redirect:/login";
            }

            Solicitud solicitud = solicitudRepository.findById(id).orElse(null);

            if (solicitud == null) {
                redirectAttributes.addFlashAttribute("error", "Solicitud no encontrada.");
                return "redirect:/empresa/alumnos-fct";
            }

            if (solicitud.getOferta() == null
                    || solicitud.getOferta().getEmpresa() == null
                    || !solicitud.getOferta().getEmpresa().getId().equals(empresa.getId())) {
                redirectAttributes.addFlashAttribute("error", "No tienes permiso para ver este CV.");
                return "redirect:/empresa/alumnos-fct";
            }

            if (solicitud.getAlumno() == null
                    || solicitud.getAlumno().getCvUrl() == null
                    || solicitud.getAlumno().getCvUrl().isBlank()) {
                redirectAttributes.addFlashAttribute("error", "Este alumno no tiene CV subido.");
                return "redirect:/empresa/alumnos-fct";
            }

            String urlFirmada = supabaseStorageService.crearUrlFirmada(solicitud.getAlumno().getCvUrl());

            return "redirect:" + urlFirmada;

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "No se pudo abrir el CV del alumno.");
            return "redirect:/empresa/alumnos-fct";
        }
    }
}