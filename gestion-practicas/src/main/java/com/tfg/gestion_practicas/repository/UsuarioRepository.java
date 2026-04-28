package com.tfg.gestion_practicas.repository;

import com.tfg.gestion_practicas.model.Usuario;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    Optional<Usuario> findByCorreo(String correo); // Significa que filtrando por su correo electrónico, puede existir o no un usuario. Optioanl evita errores por null y te obliga a comprobar si el dato existe o no antes de utilizarlo.

    Optional<Usuario> findByUsername(String username);

    boolean existsByCorreo(String correo);
    boolean existsByUsername(String username);
    @Query("SELECT u FROM Usuario u WHERE u.correo = :login OR u.username = :login")
    Optional<Usuario> encontrarPorEmailONombre(@Param("login") String login); 
}