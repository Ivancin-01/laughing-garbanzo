package com.tfg.gestion_practicas.services;
 
import com.tfg.gestion_practicas.model.Usuario;
import com.tfg.gestion_practicas.repository.UsuarioRepository;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
 
@Service
public class CustomUserDetailsService implements UserDetailsService {
 
    @Autowired
    private UsuarioRepository usuarioRepository;
 
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario user = usuarioRepository.findByCorreo(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "No se encontró ningún usuario con el email: " + email));
 
        return User.builder()
                .username(user.getCorreo())
                .password(user.getPwd())
                .roles(user.getRol().name())
                .build();
    }
}
 