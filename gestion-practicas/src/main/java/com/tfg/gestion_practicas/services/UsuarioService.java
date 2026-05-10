package com.tfg.gestion_practicas.services;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tfg.gestion_practicas.model.Alumno;
import com.tfg.gestion_practicas.model.Centro;
import com.tfg.gestion_practicas.model.Empresa;
import com.tfg.gestion_practicas.model.Rol;
import com.tfg.gestion_practicas.model.Tutor;
import com.tfg.gestion_practicas.model.TutorCentro;
import com.tfg.gestion_practicas.model.Usuario;
import com.tfg.gestion_practicas.repository.AlumnoRepository;
import com.tfg.gestion_practicas.repository.EmpresaRepository;
import com.tfg.gestion_practicas.repository.TutorRepository;
import com.tfg.gestion_practicas.repository.TutorCentroRepository;
import com.tfg.gestion_practicas.repository.UsuarioRepository;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AlumnoRepository alumnoRepository;

    @Autowired
    private TutorRepository tutorRepository;

    @Autowired
    private TutorCentroRepository tutorCentroRepository;

    @Autowired
    private EmpresaRepository empresaRepository;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @Transactional
    public Usuario registrar(Usuario u, String matricula, String dni, String departamento,
            String telefono, Centro centroSeleccionado,
            String cif, String nombreEmpresa, String sector,
            String ciudad, String telefonoEmpresa, String web,
            String emailContacto, String descripcion) {

        if (u == null) {
            throw new RuntimeException("Los datos del usuario no son válidos");
        }

        if (u.getRol() == null) {
            throw new RuntimeException("Debes seleccionar un rol");
        }

        if (usuarioRepository.existsByCorreo(u.getCorreo())) {
            throw new RuntimeException("El correo ya está en uso");
        }

        if (usuarioRepository.existsByUsername(u.getUsername())) {
            throw new RuntimeException("El nombre de usuario ya está en uso");
        }

        Rol rol = u.getRol();

        /*
         * Validamos centro solo para roles que pertenecen a un centro educativo.
         * Empresa y Admin no necesitan centro.
         */
        if ((rol == Rol.ALUMNO || rol == Rol.TUTOR || rol == Rol.TUTOR_CENTRO)
                && centroSeleccionado == null) {
            throw new RuntimeException("Debes seleccionar un centro educativo");
        }

        // Encriptamos contraseña y completamos campos automáticos
        u.setPwd(encoder.encode(u.getPwd()));
        u.setFCreacion(LocalDateTime.now());
        u.setActivo(true);

        // Guardamos primero el usuario y obtenemos el ID generado
        Usuario usuarioGuardado = usuarioRepository.saveAndFlush(u);

        System.out.println("=== REGISTRO: Usuario guardado con ID=" + usuarioGuardado.getId()
                + " ROL=" + usuarioGuardado.getRol());

        // Creamos la entidad específica según el rol
        if (rol == Rol.ALUMNO) {
            Alumno nuevoAlumno = new Alumno();

            nuevoAlumno.setUsuario(usuarioGuardado);
            nuevoAlumno.setDni(dni != null ? dni : "");
            nuevoAlumno.setMatricula(matricula != null ? matricula : "");
            nuevoAlumno.setEstadoFct("En búsqueda");

            /*
             * IMPORTANTE:
             * Esto requiere que Alumno.java tenga:
             *
             * @ManyToOne
             * 
             * @JoinColumn(name = "centro_educativo")
             * private Centro centro;
             */
            nuevoAlumno.setCentro(centroSeleccionado);

            alumnoRepository.save(nuevoAlumno);

            System.out.println("=== REGISTRO: Alumno creado OK - centro="
                    + centroSeleccionado.getNombre());

        } else if (rol == Rol.TUTOR) {
            Tutor nuevoTutor = new Tutor();

            nuevoTutor.setUsuario(usuarioGuardado);
            nuevoTutor.setDepartamento(departamento);
            nuevoTutor.setTelefono(telefono);

            /*
             * De momento mantenemos Tutor.centroEducativo como String,
             * porque vuestra página de tutor centro filtra con:
             * findByCentroEducativoIgnoreCase(tutorCentro.getNombreCentro())
             */
            nuevoTutor.setCentroEducativo(centroSeleccionado.getNombre());

            tutorRepository.save(nuevoTutor);

            System.out.println("=== REGISTRO: Tutor creado OK - centro="
                    + centroSeleccionado.getNombre());

        } else if (rol == Rol.TUTOR_CENTRO) {
            TutorCentro nuevoTutorCentro = new TutorCentro();

            nuevoTutorCentro.setUsuario(usuarioGuardado);
            nuevoTutorCentro.setTelefono(telefono);

            /*
             * De momento mantenemos TutorCentro.nombreCentro como String,
             * para no romper dashboard, tutores y asignaciones.
             */
            nuevoTutorCentro.setNombreCentro(centroSeleccionado.getNombre());

            tutorCentroRepository.save(nuevoTutorCentro);

            System.out.println("=== REGISTRO: TutorCentro creado OK - centro="
                    + centroSeleccionado.getNombre());

        } else if (rol == Rol.EMPRESA) {
            Empresa nuevaEmpresa = new Empresa();

            nuevaEmpresa.setUsuario(usuarioGuardado);
            nuevaEmpresa.setCif(cif != null ? cif : "");
            nuevaEmpresa.setNombre(nombreEmpresa != null ? nombreEmpresa : "");
            nuevaEmpresa.setSector(sector != null ? sector : "");
            nuevaEmpresa.setCiudad(ciudad != null ? ciudad : "");

            nuevaEmpresa.setEmailContacto(
                    emailContacto != null && !emailContacto.trim().isEmpty()
                            ? emailContacto
                            : usuarioGuardado.getCorreo());

            nuevaEmpresa.setTelefono(telefonoEmpresa);
            nuevaEmpresa.setWeb(web);
            nuevaEmpresa.setDescripcion(descripcion);

            empresaRepository.save(nuevaEmpresa);

            System.out.println("=== REGISTRO: Empresa creada OK - nombre=" + nombreEmpresa);

        } else {
            // ADMIN — solo se guarda el usuario, sin entidad extra
            System.out.println("=== REGISTRO: Rol " + rol + " sin entidad extra");
        }

        return usuarioGuardado;
    }
}