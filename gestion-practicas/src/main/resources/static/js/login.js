/* =====================================================
   FP Connect — login.js
   JS para el formulario de login:
     1. Mostrar/ocultar contraseña
     2. Validación básica antes de enviar
   ===================================================== */


/* -------------------------------------------------------
   Recogemos los elementos que vamos a usar
   ------------------------------------------------------- */
var formLogin   = document.getElementById("formLogin");
var campoUser   = document.getElementById("username");
var campoPwd    = document.getElementById("password");
var btnOjo      = document.getElementById("btnOjo");
var btnSubmit   = document.querySelector(".btn-submit");


/* -------------------------------------------------------
   1. MOSTRAR / OCULTAR CONTRASEÑA
   Al pulsar el ojo, cambiamos el type del input entre
   "password" (oculta) y "text" (visible).
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
   2. VALIDACIÓN ANTES DE ENVIAR EL FORMULARIO
   Comprobamos que los campos no estén vacíos y que la
   contraseña tenga al menos 6 caracteres.
   Si algo falla, mostramos el error y NO enviamos el form.
   ------------------------------------------------------- */
formLogin.addEventListener("submit", function (evento) {

  var hayErrores = false;

  // Limpiamos errores previos
  limpiarErrores();

  // USERNAME: no puede estar vacío
  var username = campoUser.value.trim();
  if (username === "") {
    mostrarError("err-username", "username", "El usuario o email es obligatorio.");
    hayErrores = true;
  }

  // CONTRASEÑA: mínimo 6 caracteres
  var pwd = campoPwd.value;
  if (pwd.length < 6) {
    mostrarError("err-password", "password", "La contraseña debe tener al menos 6 caracteres.");
    hayErrores = true;
  }

  // Si hay errores, cancelamos el envío
  if (hayErrores) {
    evento.preventDefault();

    // Hacemos foco en el primer campo con error
    var primerError = document.querySelector(".campo-error");
    if (primerError) {
      primerError.focus();
    }

    return;
  }

  // Si todo OK: desactivamos el botón para evitar doble envío
  btnSubmit.disabled = true;
  btnSubmit.textContent = "Iniciando sesión...";

});


/* -------------------------------------------------------
   LIMPIAR ERRORES al empezar a escribir en cada campo
   Mejora la experiencia de usuario: el error desaparece
   en cuanto el usuario corrige el campo.
   ------------------------------------------------------- */
campoUser.addEventListener("input", function () {
  campoUser.classList.remove("campo-error");
  document.getElementById("err-username").textContent = "";
});

campoPwd.addEventListener("input", function () {
  campoPwd.classList.remove("campo-error");
  document.getElementById("err-password").textContent = "";
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

  // Quitamos el borde rojo de todos los inputs
  var campos = document.querySelectorAll("input");
  for (var j = 0; j < campos.length; j++) {
    campos[j].classList.remove("campo-error");
  }

}
