document.addEventListener('DOMContentLoaded', function() {

  const form = document.getElementById('formRegistro');
  const rolesGrid = document.getElementById('rolesGrid');
  const rolSelect = document.getElementById('rolSelect');
  const btnOjo = document.getElementById('btnOjo');
  const pwdInput = document.getElementById('pwd');

  const seccionAlumno     = document.getElementById('seccionAlumno');
  const seccionTutor      = document.getElementById('seccionTutor');
  const seccionTutorCentro = document.getElementById('seccionTutorCentro');

  // ==========================================
  // INIT — ocultamos todas las secciones al cargar
  // ==========================================
  [seccionAlumno, seccionTutor, seccionTutorCentro].forEach(function(sec) {
    if (sec) {
      sec.style.display = 'none';
      sec.style.opacity = '0';
    }
  });

  // ==========================================
  // 1) MOSTRAR/OCULTAR CONTRASEÑA
  // ==========================================
  if (btnOjo && pwdInput) {
    btnOjo.addEventListener('click', function() {
      if (pwdInput.type === 'password') {
        pwdInput.type = 'text';
        btnOjo.textContent = '🙈';
      } else {
        pwdInput.type = 'password';
        btnOjo.textContent = '👁';
      }
    });
  }

  // ==========================================
  // 2) SELECCIÓN DE ROL
  // ==========================================
  if (rolesGrid) {
    const botonesRol = rolesGrid.querySelectorAll('.rol-btn');
    botonesRol.forEach(function(btn) {
      btn.addEventListener('click', function() {
        botonesRol.forEach(function(b) { b.classList.remove('seleccionado'); });
        this.classList.add('seleccionado');
        rolSelect.value = this.getAttribute('data-rol');
        mostrarSeccionRol(rolSelect.value);
      });
    });
  }

  // ==========================================
  // 3) MOSTRAR/OCULTAR SECCIONES POR ROL
  // ==========================================
  function mostrarSeccionRol(rol) {
    // Ocultamos todas
    [seccionAlumno, seccionTutor, seccionTutorCentro].forEach(function(sec) {
      if (sec) {
        sec.style.display = 'none';
        sec.style.opacity = '0';
      }
    });

    // Mostramos solo la del rol elegido
    var seccionActiva = null;
    if (rol === 'ALUMNO')       seccionActiva = seccionAlumno;
    if (rol === 'TUTOR')        seccionActiva = seccionTutor;
    if (rol === 'TUTOR_CENTRO') seccionActiva = seccionTutorCentro;

    if (seccionActiva) {
      seccionActiva.style.display = 'block';
      // Pequeña animación de entrada
      setTimeout(function() { seccionActiva.style.opacity = '1'; }, 10);
    }
  }

  // ==========================================
  // 4) VALIDACIÓN DEL FORMULARIO
  // ==========================================
  if (form) {
    form.addEventListener('submit', function(e) {
      var hayErrores = false;

      document.querySelectorAll('.error-msg').forEach(function(span) {
        span.textContent = '';
      });

      var username = document.getElementById('username');
      if (!username.value.trim()) {
        mostrarError('err-username', 'El username es obligatorio');
        hayErrores = true;
      }

      var correo = document.getElementById('correo');
      if (!correo.value.trim() || !validarEmail(correo.value)) {
        mostrarError('err-correo', 'Introduce un email válido');
        hayErrores = true;
      }

      var pwd = document.getElementById('pwd');
      if (!pwd.value || pwd.value.length < 6) {
        mostrarError('err-pwd', 'La contraseña debe tener al menos 6 caracteres');
        hayErrores = true;
      }

      var nombre = document.getElementById('nombre');
      if (!nombre.value.trim()) {
        mostrarError('err-nombre', 'El nombre es obligatorio');
        hayErrores = true;
      }

      var apellidos = document.getElementById('apellidos');
      if (!apellidos.value.trim()) {
        mostrarError('err-apellidos', 'Los apellidos son obligatorios');
        hayErrores = true;
      }

      var fNac = document.getElementById('fNac');
      if (!fNac.value) {
        mostrarError('err-fNac', 'La fecha de nacimiento es obligatoria');
        hayErrores = true;
      }

      if (!rolSelect.value) {
        mostrarError('err-rol', 'Debes seleccionar un rol');
        hayErrores = true;
      }

      if (rolSelect.value === 'ALUMNO') {
        var matricula = document.getElementById('matricula');
        if (!matricula.value) {
          mostrarError('err-matricula', 'Debes seleccionar una especialidad');
          hayErrores = true;
        }
        var dni = document.getElementById('dni');
        if (!dni.value.trim()) {
          mostrarError('err-dni', 'El DNI es obligatorio para alumnos');
          hayErrores = true;
        }
      }

      if (rolSelect.value === 'TUTOR') {
        var departamento = document.getElementById('departamento');
        var centroEducativo = document.getElementById('centroEducativo');
        var telefono = document.getElementById('telefono');
        if (!departamento.value.trim()) { mostrarError('err-rol', 'El departamento es obligatorio'); hayErrores = true; }
        if (!centroEducativo.value.trim()) { mostrarError('err-rol', 'El centro educativo es obligatorio'); hayErrores = true; }
        if (!telefono.value.trim()) { mostrarError('err-rol', 'El teléfono es obligatorio'); hayErrores = true; }
      }

      if (rolSelect.value === 'TUTOR_CENTRO') {
        var nombreCentro = document.getElementById('nombreCentro');
        var telefonoTC = document.getElementById('telefonoTC');
        if (!nombreCentro.value.trim()) { mostrarError('err-rol', 'El nombre del centro es obligatorio'); hayErrores = true; }
        if (!telefonoTC.value.trim()) { mostrarError('err-rol', 'El teléfono es obligatorio'); hayErrores = true; }
      }

      if (hayErrores) {
        e.preventDefault();
        return false;
      }
    });
  }

  // ==========================================
  // FUNCIONES AUXILIARES
  // ==========================================
  function mostrarError(idSpan, mensaje) {
    var span = document.getElementById(idSpan);
    if (span) span.textContent = mensaje;
  }

  function validarEmail(email) {
    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
  }
});