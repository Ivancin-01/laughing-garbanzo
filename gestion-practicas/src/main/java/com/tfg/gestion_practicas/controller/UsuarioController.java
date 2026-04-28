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

import com.tfg.gestion_practicas.model.Rol;
import com.tfg.gestion_practicas.model.Usuario;
import com.tfg.gestion_practicas.services.UsuarioService;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/registro")
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("usuario", new Usuario());
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
            @RequestParam(required = false) String matricula,
            @RequestParam(required = false) String dni,
            @RequestParam(required = false) String departamento,
            @RequestParam(required = false) String centroEducativo,
            @RequestParam(required = false) String telefono,
            @RequestParam(required = false) String nombreCentro,
            @RequestParam(required = false) String telefonoTC,
            Model model) {

        // Log para depuración — ver exactamente qué llega del formulario
        System.out.println("=== REGISTRO RECIBIDO ===");
        System.out.println("username=" + username + " correo=" + correo);
        System.out.println("rol=" + rol);
        System.out.println("nombreCentro=" + nombreCentro + " telefonoTC=" + telefonoTC);
        System.out.println("departamento=" + departamento + " centroEducativo=" + centroEducativo);
        System.out.println("telefono=" + telefono);

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

            // ✅ Para TUTOR_CENTRO usamos telefonoTC, para el resto telefono
            String telefonoFinal = (rol == Rol.TUTOR_CENTRO) ? telefonoTC : telefono;

            usuarioService.registrar(nuevoUsuario, matricula, dni, departamento,
                                     centroEducativo, telefonoFinal, nombreCentro);

            return "redirect:/login?registrado";

        } catch (RuntimeException e) {
            System.out.println("=== ERROR EN REGISTRO: " + e.getMessage());
            model.addAttribute("error", e.getMessage());
            return "registro";
        }
    }
}