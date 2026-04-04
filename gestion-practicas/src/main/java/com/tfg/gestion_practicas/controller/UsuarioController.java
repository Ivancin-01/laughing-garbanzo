package com.tfg.gestion_practicas.controller;

import com.tfg.gestion_practicas.model.Usuario;
import com.tfg.gestion_practicas.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // Muestra el formulario en http://localhost:8080/usuarios/registro
    @GetMapping("/registro")
    public String mostrarFormulario() {
        return "registro"; 
    }

    // Recibe los datos del formulario
    @PostMapping("/registrar")
public String registrarUsuario(Usuario usuario, @RequestParam(required = false) String matricula,@RequestParam(required = false) String dni) {
    try {
        usuarioService.registrar(usuario, matricula,dni);
        return "redirect:/usuarios/registro?exito";
    } catch (RuntimeException e) {
        return "redirect:/usuarios/registro?error";
    }
}
}
