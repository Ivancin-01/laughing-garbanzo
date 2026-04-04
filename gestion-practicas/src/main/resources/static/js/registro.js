/* =====================================================
   FP Connect — registro.js
   JS básico para el formulario de registro:
     1. Selector de rol (botones visuales)
     2. Mostrar/ocultar sección alumno
     3. Mostrar/ocultar contraseña
     4. Validación básica antes de enviar
   ===================================================== */


/* -------------------------------------------------------
   Recogemos los elementos que vamos a usar
   ------------------------------------------------------- */
var formRegistro   = document.getElementById("formRegistro");
var rolSelect      = document.getElementById("rolSelect");      // Select oculto
var rolesGrid      = document.getElementById("rolesGrid");      // Contenedor de botones de rol
var seccionAlumno  = document.getElementById("seccionAlumno");  // Bloque datos alumno
var btnOjo         = document.getElementById("btnOjo");         // Botón ver contraseña
var campoPwd       = document.getElementById("pwd");            // Input contraseña


/* -------------------------------------------------------
   1. SELECTOR DE ROL — botones visuales
   Cuando el usuario hace clic en uno de los cuatro botones,
   marcamos ese botón y actualizamos el select oculto.
   ------------------------------------------------------- */

// Recogemos todos los botones de rol del grid
var botonesRol = rolesGrid.getElementsByClassName("rol-btn");

// Recorremos los botones y añadimos un evento click a cada uno
for (var i = 0; i < botonesRol.length; i++) {

  botonesRol[i].addEventListener("click", function () {

    // 1a. Quitamos la clase "seleccionado" a todos los botones
    for (var j = 0; j < botonesRol.length; j++) {
      botonesRol[j].classList.remove("seleccionado");
    }

    // 1b. Añadimos "seleccionado" solo al que se ha pulsado
    this.classList.add("seleccionado");

    // 1c. Leemos el rol del atributo data-rol del botón
    var rolElegido = this.getAttribute("data-rol");

    // 1d. Actualizamos el select oculto para que Thymeleaf reciba el valor correcto
    rolSelect.value = rolElegido;

    // 1e. Limpiamos el mensaje de error del rol si había uno
    document.getElementById("err-rol").textContent = "";

    // 1f. Mostramos u ocultamos la sección de alumno según el rol
    mostrarSeccionAlumno(rolElegido);

  });
}


/* -------------------------------------------------------
   2. MOSTRAR / OCULTAR SECCIÓN ALUMNO
   Esta función se llama desde el evento click de los botones.
   ------------------------------------------------------- */
function mostrarSeccionAlumno(rol) {

  if (rol === "ALUMNO") {
    // Añadimos la clase que en CSS pone display:block y opacity:1
    seccionAlumno.classList.add("seccion-visible");
  } else {
    seccionAlumno.classList.remove("seccion-visible");
  }

}


/* -------------------------------------------------------
   3. MOSTRAR / OCULTAR CONTRASEÑA
   Al pulsar el ojo, cambiamos el type del input entre
   "password" (oculta) y "text" (visible).
   ------------------------------------------------------- */
btnOjo.addEventListener("click", function () {

  if (campoPwd.type === "password") {
    campoPwd.type = "text";       // Mostramos el texto
    btnOjo.textContent = "🙈";    // Cambiamos el icono
  } else {
    campoPwd.type = "password";   // Volvemos a ocultar
    btnOjo.textContent = "👁";    // Icono original
  }

});


/* -------------------------------------------------------
   4. VALIDACIÓN ANTES DE ENVIAR EL FORMULARIO
   Comprobamos los campos más importantes.
   Si algo falla, mostramos el error y NO enviamos el form.
   ------------------------------------------------------- */

formRegistro.addEventListener("submit", function (evento) {

  // Variable para saber si hubo algún error
  var hayErrores = false;

  // Limpiamos todos los mensajes de error y bordes rojos previos
  limpiarErrores();

  // --- Validación campo por campo ---

  // USERNAME: no puede estar vacío
  var username = document.getElementById("username").value.trim();
  if (username === "") {
    mostrarError("err-username", "username", "El username es obligatorio.");
    hayErrores = true;
  }

  // NOMBRE: no puede estar vacío
  var nombre = document.getElementById("nombre").value.trim();
  if (nombre === "") {
    mostrarError("err-nombre", "nombre", "El nombre es obligatorio.");
    hayErrores = true;
  }

  // APELLIDOS: no puede estar vacío
  var apellidos = document.getElementById("apellidos").value.trim();
  if (apellidos === "") {
    mostrarError("err-apellidos", "apellidos", "Los apellidos son obligatorios.");
    hayErrores = true;
  }

  // FECHA DE NACIMIENTO: no puede estar vacía
  var fNac = document.getElementById("fNac").value;
  if (fNac === "") {
    mostrarError("err-fNac", "fNac", "La fecha de nacimiento es obligatoria.");
    hayErrores = true;
  }

  // EMAIL: no puede estar vacío
  var correo = document.getElementById("correo").value.trim();
  if (correo === "") {
    mostrarError("err-correo", "correo", "El email es obligatorio.");
    hayErrores = true;
  }

  // CONTRASEÑA: mínimo 6 caracteres
  var pwd = document.getElementById("pwd").value;
  if (pwd.length < 6) {
    mostrarError("err-pwd", "pwd", "La contraseña debe tener al menos 6 caracteres.");
    hayErrores = true;
  }

  // ROL: debe haberse seleccionado uno
  var rol = rolSelect.value;
  if (rol === "") {
    document.getElementById("err-rol").textContent = "Debes seleccionar un rol.";
    hayErrores = true;
  }

  // DNI (solo si el rol es ALUMNO): validación de formato básico
  if (rol === "ALUMNO") {
    var dni = document.getElementById("dni").value.trim();
    // Expresión regular básica: 8 dígitos seguidos de una letra
    var regexDNI = /^\d{8}[A-Za-z]$/;
    if (dni === "") {
      mostrarError("err-dni", "dni", "El DNI es obligatorio para alumnos.");
      hayErrores = true;
    } else if (!regexDNI.test(dni)) {
      mostrarError("err-dni", "dni", "Formato de DNI no válido (ej: 12345678A).");
      hayErrores = true;
    }
  }

  // Si hay errores, cancelamos el envío del formulario
  if (hayErrores) {
    evento.preventDefault(); // Detenemos el submit

    // Hacemos scroll al primer campo con error para que el usuario lo vea
    var primerError = document.querySelector(".campo-error");
    if (primerError) {
      primerError.focus();
    }
  }

});


/* -------------------------------------------------------
   FUNCIONES AUXILIARES
   ------------------------------------------------------- */

// Muestra un mensaje de error y marca el campo en rojo
function mostrarError(idMensaje, idCampo, texto) {
  document.getElementById(idMensaje).textContent = texto;
  document.getElementById(idCampo).classList.add("campo-error");
}

// Limpia todos los mensajes y bordes de error del formulario
function limpiarErrores() {

  // Vaciamos todos los span de error
  var mensajes = document.getElementsByClassName("error-msg");
  for (var i = 0; i < mensajes.length; i++) {
    mensajes[i].textContent = "";
  }

  // Quitamos el borde rojo de todos los inputs y selects
  var campos = document.querySelectorAll("input, select");
  for (var j = 0; j < campos.length; j++) {
    campos[j].classList.remove("campo-error");
  }
}