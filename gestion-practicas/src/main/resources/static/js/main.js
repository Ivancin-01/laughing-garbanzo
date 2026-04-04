/* =====================================================
   FP Connect — main.js
   Usamos jQuery para el carrusel y eventos básicos
   para el navbar.
   ===================================================== */


/* -------------------------------------------------------
   1. NAVBAR — cambia de aspecto al hacer scroll
   ------------------------------------------------------- */

// Guardamos el elemento navbar en una variable
var navbar = document.getElementById("navbar");

// Añadimos un listener al evento "scroll" de la ventana
window.addEventListener("scroll", function () {

  // Si el usuario ha bajado más de 40px, añadimos la clase
  if (window.scrollY > 40) {
    navbar.classList.add("navbar--scrolled");
  } else {
    navbar.classList.remove("navbar--scrolled");
  }

});


/* -------------------------------------------------------
   2. CARRUSEL — controlado con jQuery
   ------------------------------------------------------- */

// Esperamos a que jQuery y el DOM estén listos
$(document).ready(function () {

  // --- Variables del carrusel ---
  var pista         = $("#carrusel-pista");      // La pista que se desplaza
  var contenedorPuntos = $("#carrusel-puntos");  // Donde pondremos los puntos
  var anchaTarjeta  = 338;   // Ancho de cada tarjeta (igual que en CSS: min-width)
  var gap           = 24;    // Espacio entre tarjetas (igual que en CSS: gap)
  var paso          = anchaTarjeta + gap;        // Cuánto desplazamos en cada clic
  var indiceActual  = 0;     // Tarjeta en la que estamos
  var totalTarjetas = $(".carrusel-tarjeta").length; // Contamos las tarjetas
  var tarjetasVisibles = 2;  // Cuántas se ven a la vez en la ventana

  // El máximo índice al que podemos llegar sin mostrar tarjetas vacías
  var indiceMaximo = totalTarjetas - tarjetasVisibles;

  // --- Generamos los puntos indicadores dinámicamente ---
  for (var i = 0; i <= indiceMaximo; i++) {
    // Creamos un botón por cada posición posible del carrusel
    var punto = $("<button></button>").addClass("punto");
    if (i === 0) {
      punto.addClass("activo"); // El primero empieza activo
    }
    contenedorPuntos.append(punto);
  }

  // Guardamos todos los puntos en una variable para usarlos luego
  var puntos = $(".punto");

  // --- Función que mueve el carrusel a un índice concreto ---
  function irA(nuevoIndice) {

    // Nos aseguramos de que el índice esté dentro del rango permitido
    if (nuevoIndice < 0) {
      nuevoIndice = 0;
    }
    if (nuevoIndice > indiceMaximo) {
      nuevoIndice = indiceMaximo;
    }

    // Actualizamos el índice actual
    indiceActual = nuevoIndice;

    // Calculamos cuántos píxeles hay que desplazar la pista
    var desplazamiento = indiceActual * paso;

    // Animamos el margen izquierdo de la pista con jQuery UI
    // (easing "easeInOutQuad" es una curva de aceleración suave)
    pista.animate(
      { marginLeft: -desplazamiento + "px" },
      400,               // duración en milisegundos
      "easeInOutQuad"    // tipo de animación (de jQuery UI)
    );

    // Actualizamos los puntos: quitamos "activo" a todos y se lo ponemos al actual
    puntos.removeClass("activo");
    puntos.eq(indiceActual).addClass("activo");

  }

  // --- Botón SIGUIENTE ---
  $("#btn-siguiente").on("click", function () {
    irA(indiceActual + 1);
  });

  // --- Botón ANTERIOR ---
  $("#btn-anterior").on("click", function () {
    irA(indiceActual - 1);
  });

  // --- Clic en un punto ---
  // Usamos delegación de eventos porque los puntos se crearon dinámicamente
  contenedorPuntos.on("click", ".punto", function () {
    // Obtenemos el índice del punto pulsado con .index()
    var indicePunto = $(this).index();
    irA(indicePunto);
  });

}); // fin document.ready
