package com.tfg.gestion_practicas.services;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tfg.gestion_practicas.model.Alumno;
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
            String centroEducativo, String telefono, String nombreCentro,
            String cif, String nombreEmpresa, String sector,
            String ciudad, String telefonoEmpresa, String web,
            String emailContacto, String descripcion) {

        if (u == null)
            throw new RuntimeException("Los datos del usuario no son válidos");
        if (u.getRol() == null)
            throw new RuntimeException("Debes seleccionar un rol");

        if (usuarioRepository.existsByCorreo(u.getCorreo()))
            throw new RuntimeException("El correo ya está en uso");
        if (usuarioRepository.existsByUsername(u.getUsername()))
            throw new RuntimeException("El nombre de usuario ya está en uso");

        // Encriptamos contraseña y completamos campos automáticos
        u.setPwd(encoder.encode(u.getPwd()));
        u.setFCreacion(LocalDateTime.now());
        u.setActivo(true);

        // ✅ Guardamos primero el usuario y obtenemos el objeto con ID generado
        Usuario usuarioGuardado = usuarioRepository.saveAndFlush(u);

        System.out.println("=== REGISTRO: Usuario guardado con ID=" + usuarioGuardado.getId()
                + " ROL=" + usuarioGuardado.getRol());

        // ✅ Creamos la entidad específica según el rol
        Rol rol = usuarioGuardado.getRol();

        if (rol == Rol.ALUMNO) {
            Alumno nuevoAlumno = new Alumno();
            nuevoAlumno.setUsuario(usuarioGuardado);
            nuevoAlumno.setDni(dni != null ? dni : "");
            nuevoAlumno.setMatricula(matricula != null ? matricula : "");
            nuevoAlumno.setEstadoFct("En búsqueda");
            alumnoRepository.save(nuevoAlumno);
            System.out.println("=== REGISTRO: Alumno creado OK");

        } else if (rol == Rol.TUTOR) {
            Tutor nuevoTutor = new Tutor();
            nuevoTutor.setUsuario(usuarioGuardado);
            nuevoTutor.setDepartamento(departamento);
            nuevoTutor.setCentroEducativo(centroEducativo);
            nuevoTutor.setTelefono(telefono);
            tutorRepository.save(nuevoTutor);
            System.out.println("=== REGISTRO: Tutor creado OK");

        } else if (rol == Rol.TUTOR_CENTRO) {
            // ✅ nombreCentro viene del campo "nombreCentro" del formulario
            // Si viene vacío, usamos centroEducativo como fallback
            String centro = (nombreCentro != null && !nombreCentro.trim().isEmpty())
                    ? nombreCentro
                    : centroEducativo;

            TutorCentro nuevoTutorCentro = new TutorCentro();
            nuevoTutorCentro.setUsuario(usuarioGuardado);
            nuevoTutorCentro.setNombreCentro(centro);
            nuevoTutorCentro.setTelefono(telefono);
            tutorCentroRepository.save(nuevoTutorCentro);
            System.out.println("=== REGISTRO: TutorCentro creado OK - centro=" + centro);

        } else if (rol == Rol.EMPRESA) {
            Empresa nuevaEmpresa = new Empresa();
            nuevaEmpresa.setUsuario(usuarioGuardado);
            nuevaEmpresa.setCif(cif != null ? cif : "");
            nuevaEmpresa.setNombre(nombreEmpresa != null ? nombreEmpresa : "");
            nuevaEmpresa.setSector(sector != null ? sector : "");
            nuevaEmpresa.setCiudad(ciudad != null ? ciudad : "");
            // Use provided emailContacto, fallback to user's correo
            nuevaEmpresa.setEmailContacto((emailContacto != null && !emailContacto.trim().isEmpty()) ? emailContacto
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