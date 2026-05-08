/*package com.tfg.gestion_practicas.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tfg.gestion_practicas.model.Alumno;

@Repository
public interface AlumnoRepository extends JpaRepository<Alumno, Long> {
    Optional<Alumno> findByUsuarioId(Long usuarioId);

    List<Alumno> findByTutorId(Long TutorId);

    Optional<Alumno> findByUsuarioCorreo(String correo);
}*/

package com.tfg.gestion_practicas.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.tfg.gestion_practicas.model.Alumno;

@Repository
public interface AlumnoRepository extends JpaRepository<Alumno, Long> {

    Optional<Alumno> findByUsuarioId(Long usuarioId);

    List<Alumno> findByTutorId(Long TutorId);

    Optional<Alumno> findByUsuarioCorreo(String correo);

    Optional<Alumno> findByUsuarioUsername(String username);

}
