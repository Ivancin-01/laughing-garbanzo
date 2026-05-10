package com.tfg.gestion_practicas.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.tfg.gestion_practicas.model.Centro;
import com.tfg.gestion_practicas.model.Rol;
import com.tfg.gestion_practicas.model.Usuario;
import com.tfg.gestion_practicas.repository.CentroRepository;
import com.tfg.gestion_practicas.services.UsuarioService;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private CentroRepository centroRepository;

    @GetMapping("/registro")
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("centros", centroRepository.findAllByOrderByNombreAsc());
        return "registro";
    }

    @PostMapping("/registrar")
    public String registrar(
            @RequestParam String username,
            @RequestParam String correo,
            @RequestParam String pwd,
            @RequestParam String nombre,
            @RequestParam String apellidos,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fNac,
            @RequestParam Rol rol,

            // Datos alumno
            @RequestParam(required = false) String matricula,
            @RequestParam(required = false) String dni,

            // Datos tutor
            @RequestParam(required = false) String departamento,
            @RequestParam(required = false) String telefono,

            // Datos tutor centro
            @RequestParam(required = false) String telefonoTC,

            // Datos empresa
            @RequestParam(required = false) String cif,
            @RequestParam(required = false) String nombreEmpresa,
            @RequestParam(required = false) String sector,
            @RequestParam(required = false) String ciudad,
            @RequestParam(required = false) String telefonoEmpresa,
            @RequestParam(required = false) String web,
            @RequestParam(required = false) String emailContacto,
            @RequestParam(required = false) String descripcion,

            // Nuevos selects de centro
            @RequestParam(value = "centroIdAlumno", required = false) Long centroIdAlumno,
            @RequestParam(value = "centroIdTutor", required = false) Long centroIdTutor,
            @RequestParam(value = "centroIdTutorCentro", required = false) Long centroIdTutorCentro,

            Model model) {

        System.out.println("=== REGISTRO RECIBIDO ===");
        System.out.println("username=" + username + " correo=" + correo);
        System.out.println("rol=" + rol);
        System.out.println("centroIdAlumno=" + centroIdAlumno);
        System.out.println("centroIdTutor=" + centroIdTutor);
        System.out.println("centroIdTutorCentro=" + centroIdTutorCentro);

        try {
            Usuario nuevoUsuario = Usuario.builder()
                    .username(username)
                    .correo(correo)
                    .pwd(pwd)
                    .nombre(nombre)
                    .apellidos(apellidos)
                    .fNac(fNac)
                    .rol(rol)
                    .activo(true)
                    .build();

            Long centroIdSeleccionado = null;

            if (rol == Rol.ALUMNO) {
                centroIdSeleccionado = centroIdAlumno;
            } else if (rol == Rol.TUTOR) {
                centroIdSeleccionado = centroIdTutor;
            } else if (rol == Rol.TUTOR_CENTRO) {
                centroIdSeleccionado = centroIdTutorCentro;
            }

            Centro centroSeleccionado = null;

            if (rol == Rol.ALUMNO || rol == Rol.TUTOR || rol == Rol.TUTOR_CENTRO) {
                if (centroIdSeleccionado == null) {
                    throw new RuntimeException("Debes seleccionar un centro educativo.");
                }

                centroSeleccionado = centroRepository.findById(centroIdSeleccionado)
                        .orElseThrow(() -> new RuntimeException("Centro educativo no encontrado."));
            }

            String telefonoFinal = (rol == Rol.TUTOR_CENTRO) ? telefonoTC : telefono;

            usuarioService.registrar(
                    nuevoUsuario,
                    matricula,
                    dni,
                    departamento,
                    telefonoFinal,
                    centroSeleccionado,
                    cif,
                    nombreEmpresa,
                    sector,
                    ciudad,
                    telefonoEmpresa,
                    web,
                    emailContacto,
                    descripcion);

            return "redirect:/login?registrado";

        } catch (RuntimeException e) {
            System.out.println("=== ERROR EN REGISTRO: " + e.getMessage());

            model.addAttribute("usuario", new Usuario());
            model.addAttribute("centros", centroRepository.findAllByOrderByNombreAsc());
            model.addAttribute("error", e.getMessage());

            return "registro";
        }
    }
}