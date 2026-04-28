/*package com.tfg.gestion_practicas.services;

import com.tfg.gestion_practicas.controller.AlumnoController;
import com.tfg.gestion_practicas.model.Alumno;
import com.tfg.gestion_practicas.model.Usuario;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.tfg.gestion_practicas.repository.AlumnoRepository;

 Este servicio se va a utilizar para hacer funcionar el login. 
        Por el momento, solo funcionará con la tabla "alumnos" de nuestra base de datos en SupaBase.



@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final AlumnoController alumnoController;
    
    @Autowired
    private AlumnoRepository alumnoRepository;

    CustomUserDetailsService(AlumnoController alumnoController) {
        this.alumnoController = alumnoController;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 1. Buscamos a un alumno por su email. 
        Alumno alu = alumnoRepository.findByUsuarioCorreo(email).orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email " + email));

        // 2. Accedemos al objeto usuario relacionado con Alumno para obtener la contraseña.
        Usuario user = alu.getUsuario();

        // 3. Devolvemos un objeto USER de Spring Security. 
        // IMPORTANCIA: Aquí usamos como 'username' al EMAIL y la CONTRASEÑA de la DB.
        return User.builder()
                .username(user.getCorreo())
                .password(user.getPwd())
                .roles(user.getRol().name())
                .build();
    }
}*/

package com.tfg.gestion_practicas.services;

import com.tfg.gestion_practicas.model.Alumno;
import com.tfg.gestion_practicas.model.Usuario;
import com.tfg.gestion_practicas.repository.AlumnoRepository;
import com.tfg.gestion_practicas.repository.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/* Este servicio permite el login usando tanto el correo como el nombre de usuario.
*/

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository; // <--- CAMBIA AlumnoRepository por UsuarioRepository

    @Override
    public UserDetails loadUserByUsername(String loginInput) throws UsernameNotFoundException {
        // Buscamos directamente al usuario global (sirve para todos los roles)
        Usuario user = usuarioRepository.encontrarPorEmailONombre(loginInput)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + loginInput));
                
            return User.builder()
                .username(user.getCorreo())
                .password(user.getPwd())
                .roles(user.getRol().name())
                .build();
    }
}
