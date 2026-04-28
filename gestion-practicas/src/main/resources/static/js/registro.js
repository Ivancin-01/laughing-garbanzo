/* =====================================================
   FP Connect — registro.js (VERSIÓN ARREGLADA)
   ===================================================== */

document.addEventListener("DOMContentLoaded", function () {

  /* -------------------------------------------------------
     ELEMENTOS DEL DOM
     ------------------------------------------------------- */
  var formRegistro   = document.getElementById("formRegistro");
  var rolSelect      = document.getElementById("rolSelect");
  var rolesGrid      = document.getElementById("rolesGrid");
  var seccionAlumno  = document.getElementById("seccionAlumno");
  var seccionTutor   = document.getElementById("seccionTutor");
  var seccionEmpresa = document.getElementById("seccionEmpresa");
  var btnOjo         = document.getElementById("btnOjo");
  var campoPwd       = document.getElementById("pwd");

  console.log("JS cargado correctamente"); // DEBUG

  /* -------------------------------------------------------
     1. SELECTOR DE ROL
     ------------------------------------------------------- */
  var botonesRol = rolesGrid.getElementsByClassName("rol-btn");

  for (var i = 0; i < botonesRol.length; i++) {
    botonesRol[i].addEventListener("click", function () {

      // Quitar selección previa
      for (var j = 0; j < botonesRol.length; j++) {
        botonesRol[j].classList.remove("seleccionado");
      }

      // Marcar seleccionado
      this.classList.add("seleccionado");

      // Obtener rol (seguro)
      var rolElegido = this.getAttribute("data-rol").trim().toUpperCase();
      console.log("Rol elegido:", rolElegido);

      // Guardar en el select oculto
      rolSelect.value = rolElegido;

      // Limpiar error
      document.getElementById("err-rol").textContent = "";

      // Mostrar sección correspondiente
      mostrarSecciones(rolElegido);
    });
  }

  /* -------------------------------------------------------
     MOSTRAR SECCIONES
     ------------------------------------------------------- */
  function mostrarSecciones(rol) {

    // Ocultar todas
    seccionAlumno.classList.remove("seccion-visible");
    seccionTutor.classList.remove("seccion-visible");
    seccionEmpresa.classList.remove("seccion-visible");

    // Mostrar solo una
    if (rol === "ALUMNO") {
      seccionAlumno.classList.add("seccion-visible");
    } else if (rol === "TUTOR") {
      seccionTutor.classList.add("seccion-visible");
    } else if (rol === "EMPRESA") {
      seccionEmpresa.classList.add("seccion-visible");
    }
  }

  /* -------------------------------------------------------
     3. MOSTRAR / OCULTAR CONTRASEÑA
     ------------------------------------------------------- */
  btnOjo.addEventListener("click", function () {
    if (campoPwd.type === "password") {
      campoPwd.type = "text";
      btnOjo.textContent = "🙈";
    } else {
      campoPwd.type = "password";
      btnOjo.textContent = "👁";
    }
  });

  /* -------------------------------------------------------
     4. VALIDACIÓN FORMULARIO
     ------------------------------------------------------- */
  formRegistro.addEventListener("submit", function (evento) {

    var hayErrores = false;
    limpiarErrores();

    var username = document.getElementById("username").value.trim();
    if (username === "") {
      mostrarError("err-username", "username", "El username es obligatorio.");
      hayErrores = true;
    }

    var nombre = document.getElementById("nombre").value.trim();
    if (nombre === "") {
      mostrarError("err-nombre", "nombre", "El nombre es obligatorio.");
      hayErrores = true;
    }

    var apellidos = document.getElementById("apellidos").value.trim();
    if (apellidos === "") {
      mostrarError("err-apellidos", "apellidos", "Los apellidos son obligatorios.");
      hayErrores = true;
    }

    var fNac = document.getElementById("fNac").value;
    if (fNac === "") {
      mostrarError("err-fNac", "fNac", "La fecha de nacimiento es obligatoria.");
      hayErrores = true;
    }

    var correo = document.getElementById("correo").value.trim();
    if (correo === "") {
      mostrarError("err-correo", "correo", "El email es obligatorio.");
      hayErrores = true;
    }

    var pwd = document.getElementById("pwd").value;
    if (pwd.length < 6) {
      mostrarError("err-pwd", "pwd", "La contraseña debe tener al menos 6 caracteres.");
      hayErrores = true;
    }

    var rol = rolSelect.value;
    if (rol === "") {
      document.getElementById("err-rol").textContent = "Debes seleccionar un rol.";
      hayErrores = true;
    }

    // Validación DNI solo alumno
    if (rol === "ALUMNO") {
      var dni = document.getElementById("dni").value.trim();
      var regexDNI = /^\d{8}[A-Za-z]$/;

      if (dni === "") {
        mostrarError("err-dni", "dni", "El DNI es obligatorio.");
        hayErrores = true;
      } else if (!regexDNI.test(dni)) {
        mostrarError("err-dni", "dni", "Formato no válido.");
        hayErrores = true;
      }
    }

    if (hayErrores) {
      evento.preventDefault();
      var primerError = document.querySelector(".campo-error");
      if (primerError) primerError.focus();
    }
  });

  /* -------------------------------------------------------
     FUNCIONES AUXILIARES
     ------------------------------------------------------- */
  function mostrarError(idMensaje, idCampo, texto) {
    document.getElementById(idMensaje).textContent = texto;
    document.getElementById(idCampo).classList.add("campo-error");
  }

  function limpiarErrores() {
    var mensajes = document.getElementsByClassName("error-msg");
    for (var i = 0; i < mensajes.length; i++) {
      mensajes[i].textContent = "";
    }

    var campos = document.querySelectorAll("input, select");
    for (var j = 0; j < campos.length; j++) {
      campos[j].classList.remove("campo-error");
    }
  }

});