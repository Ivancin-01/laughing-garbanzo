package com.tfg.gestion_practicas.controller;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import com.tfg.gestion_practicas.model.Empresa;
import com.tfg.gestion_practicas.model.EstadoSolicitud;
import com.tfg.gestion_practicas.model.Oferta;
import com.tfg.gestion_practicas.model.Solicitud;
import com.tfg.gestion_practicas.repository.EmpresaRepository;
import com.tfg.gestion_practicas.repository.OfertaRepository;
import com.tfg.gestion_practicas.repository.SolicitudRepository;

@Controller
public class EmpresaController {

    @Autowired
    private EmpresaRepository empresaRepository;

    @Autowired
    private OfertaRepository ofertaRepository;

    @Autowired
    private SolicitudRepository solicitudRepository;

    @GetMapping("/empresa/dashboard")
    public String dashboardEmpresa(Model model, Principal principal) {
        if (principal == null)
            return "redirect:/login";

        String email = principal.getName();
        Empresa empresa = empresaRepository.findByUsuarioCorreo(email).orElse(null);

        if (empresa == null)
            return "redirect:/login?error=empresa-no-encontrada";

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
        if (principal == null)
            return "redirect:/login";

        String email = principal.getName();
        Empresa empresa = empresaRepository.findByUsuarioCorreo(email).orElse(null);

        if (empresa == null)
            return "redirect:/login";

        model.addAttribute("empresa", empresa);
        model.addAttribute("oferta", new Oferta());

        return "empresa/crear-oferta";
    }

    @PostMapping("/empresa/ofertas/guardar")
    public String guardarOferta(@ModelAttribute("oferta") Oferta oferta, Principal principal) {
        if (principal == null)
            return "redirect:/login";

        String email = principal.getName();
        Empresa empresa = empresaRepository.findByUsuarioCorreo(email).orElse(null);

        if (empresa != null) {
            oferta.setEmpresa(empresa);
            oferta.setFechaPublicacion(LocalDate.now());
            // Si el formulario no envía ciudad, podrías usar la de la empresa:
            if (oferta.getCiudad() == null || oferta.getCiudad().isEmpty()) {
                oferta.setCiudad(empresa.getCiudad());
            }
            ofertaRepository.save(oferta);
        }

        return "redirect:/empresa/dashboard?creada=true";
    }

    @GetMapping("/empresa/perfil")
    public String mostrarPerfil(Model model, Principal principal) {
        if (principal == null)
            return "redirect:/login";

        String email = principal.getName();
        Empresa empresa = empresaRepository.findByUsuarioCorreo(email).orElse(null);

        if (empresa == null)
            return "redirect:/login";

        // También obtenemos las ofertas para la estadística del perfil
        List<Oferta> ofertasActivas = ofertaRepository.findByEmpresaId(empresa.getId());

        model.addAttribute("empresa", empresa);
        model.addAttribute("ofertasActivas", ofertasActivas);

        return "empresa/perfil";
    }

    @PostMapping("/empresa/perfil/actualizar")
    public String actualizarPerfil(@ModelAttribute("empresa") Empresa empresaEditada, Principal principal) {
        if (principal == null)
            return "redirect:/login";

        // Recuperamos la empresa original para no perder datos sensibles (como la
        // relación con Usuario)
        Empresa empresaOriginal = empresaRepository.findById(empresaEditada.getId()).orElse(null);

        if (empresaOriginal != null) {
            // Actualizamos solo los campos permitidos
            empresaOriginal.setNombre(empresaEditada.getNombre());
            empresaOriginal.setCif(empresaEditada.getCif());
            empresaOriginal.setTelefono(empresaEditada.getTelefono());
            empresaOriginal.setCiudad(empresaEditada.getCiudad());
            empresaOriginal.setSector(empresaEditada.getSector());

            empresaRepository.save(empresaOriginal);
        }

        return "redirect:/empresa/perfil?exito=true";
    }

    @GetMapping("/empresa/ofertas")

    public String listarOfertas(Model model, Principal principal) {
        if (principal == null)
            return "redirect:/login";

        String email = principal.getName();
        Empresa empresa = empresaRepository.findByUsuarioCorreo(email).orElse(null);

        if (empresa == null)
            return "redirect:/login";

        List<Oferta> ofertas = ofertaRepository.findByEmpresaId(empresa.getId());

        model.addAttribute("empresa", empresa);
        model.addAttribute("ofertas", ofertas);
        return "empresa/ofertas"; // Nombre del archivo HTML
    }

    @PostMapping("/empresa/ofertas/eliminar/{id}")
    public String eliminarOferta(@PathVariable Long id, Principal principal) {
        if (principal == null)
            return "redirect:/login";

        // Verificamos que la oferta pertenezca a la empresa actual por seguridad
        String email = principal.getName();
        Oferta oferta = ofertaRepository.findById(id).orElse(null);

        if (oferta != null && oferta.getEmpresa().getUsuario().getCorreo().equals(email)) {
            ofertaRepository.delete(oferta);
        }

        return "redirect:/empresa/ofertas?eliminada=true";
    }

    @GetMapping("/empresa/ofertas/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model, Principal principal) {
        if (principal == null)
            return "redirect:/login";

        String email = principal.getName();
        Empresa empresa = empresaRepository.findByUsuarioCorreo(email).orElse(null);
        Oferta oferta = ofertaRepository.findById(id).orElse(null);

        // Seguridad: Verificar que la oferta existe y pertenece a la empresa
        if (oferta == null || !oferta.getEmpresa().getId().equals(empresa.getId())) {
            return "redirect:/empresa/ofertas?error=no-autorizado";
        }

        model.addAttribute("empresa", empresa);
        model.addAttribute("oferta", oferta);
        return "empresa/editar-oferta";
    }

    @PostMapping("/empresa/ofertas/actualizar")
    public String actualizarOferta(@ModelAttribute("oferta") Oferta ofertaEditada, Principal principal) {
        if (principal == null)
            return "redirect:/login";

        // Recuperamos la versión original para no perder la relación con la empresa y
        // la fecha de creación
        Oferta ofertaOriginal = ofertaRepository.findById(ofertaEditada.getId()).orElse(null);

        if (ofertaOriginal != null) {
            ofertaOriginal.setTitulo(ofertaEditada.getTitulo());
            ofertaOriginal.setDescripcion(ofertaEditada.getDescripcion());
            ofertaOriginal.setModalidad(ofertaEditada.getModalidad());
            ofertaOriginal.setPlazas(ofertaEditada.getPlazas());

            ofertaRepository.save(ofertaOriginal);
        }

        return "redirect:/empresa/ofertas?editada=true";
    }

    @GetMapping("/empresa/solicitudes")
    public String listarSolicitudes(Model model, Principal principal) {
        if (principal == null)
            return "redirect:/login";

        String email = principal.getName();
        Empresa empresa = empresaRepository.findByUsuarioCorreo(email).orElse(null);

        if (empresa == null)
            return "redirect:/login";

        // Buscamos todas las solicitudes cuyas ofertas pertenezcan a esta empresa
        List<Solicitud> solicitudes = solicitudRepository.findByOfertaEmpresaId(empresa.getId());

        model.addAttribute("empresa", empresa);
        model.addAttribute("solicitudes", solicitudes);
        return "empresa/solicitudes";
    }

    // MÉTODO ÚNICO PARA ACEPTAR O RECHAZAR
    @PostMapping("/empresa/solicitudes/gestionar")
    public String gestionarSolicitud(@RequestParam("solicitudId") Long id,
            @RequestParam("nuevoEstado") String estadoStr) {

        // 1. Buscar la solicitud por ID
        Solicitud solicitud = solicitudRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID de solicitud no válido: " + id));

        try {
            // 2. Convertir el String (ACEPTADO/RECHAZADO) al Enum correspondiente
            // valueOf busca la coincidencia exacta en mayúsculas
            solicitud.setEstado(EstadoSolicitud.valueOf(estadoStr.toUpperCase()));

            // 3. Guardar en la base de datos
            solicitudRepository.save(solicitud);

        } catch (IllegalArgumentException e) {
            // Manejar error si el String enviado no coincide con el Enum
            return "redirect:/empresa/solicitudes?error=estado_no_valido";
        }

        // 4. Redirigir a la lista para ver el cambio
        return "redirect:/empresa/solicitudes";
    }

    @GetMapping("/empresa/alumnos-fct")
    public String listarAlumnosEnPracticas(Model model, Principal principal) {
        if (principal == null)
            return "redirect:/login";

        String email = principal.getName();
        Empresa empresa = empresaRepository.findByUsuarioCorreo(email).orElse(null);

        if (empresa == null)
            return "redirect:/login";

        // Filtramos por el ID de la empresa y el estado "ACEPTADO" del Enum
        List<Solicitud> alumnosPracticas = solicitudRepository.findByOfertaEmpresaIdAndEstado(
                empresa.getId(),
                EstadoSolicitud.ACEPTADA);

        model.addAttribute("empresa", empresa);
        model.addAttribute("inscripciones", alumnosPracticas);
        return "empresa/alumnos-fct";
    }
}