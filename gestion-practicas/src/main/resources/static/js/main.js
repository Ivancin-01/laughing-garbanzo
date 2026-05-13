/* =====================================================
   FP Connect — main.js
   Navbar, carrusel responsive y efecto wow con ratón
   ===================================================== */


/* -------------------------------------------------------
   1. NAVBAR — cambia de aspecto al hacer scroll
   ------------------------------------------------------- */

var navbar = document.getElementById("navbar");

window.addEventListener("scroll", function () {
  if (window.scrollY > 40) {
    navbar.classList.add("navbar--scrolled");
  } else {
    navbar.classList.remove("navbar--scrolled");
  }
});


/* -------------------------------------------------------
   2. MENÚ HAMBURGUESA RESPONSIVE
   ------------------------------------------------------- */

var navbarToggle = document.getElementById("navbar-toggle");
var navbarMenu = document.getElementById("navbar-menu");

if (navbarToggle && navbarMenu) {
  navbarToggle.addEventListener("click", function () {
    navbarToggle.classList.toggle("activo");
    navbarMenu.classList.toggle("abierto");
  });

  var enlacesMenu = navbarMenu.querySelectorAll("a");

  enlacesMenu.forEach(function (enlace) {
    enlace.addEventListener("click", function () {
      navbarToggle.classList.remove("activo");
      navbarMenu.classList.remove("abierto");
    });
  });
}


/* -------------------------------------------------------
   3. CARRUSEL RESPONSIVE — controlado con jQuery
   ------------------------------------------------------- */

$(document).ready(function () {

  var pista = $("#carrusel-pista");
  var contenedorPuntos = $("#carrusel-puntos");
  var tarjetas = $(".carrusel-tarjeta");

  var indiceActual = 0;
  var totalTarjetas = tarjetas.length;
  var puntos = $();

  function calcularMedidas() {
    var anchoVentana = $(".carrusel-ventana").outerWidth();
    var anchoTarjeta = tarjetas.first().outerWidth();
    var gap = parseInt(pista.css("gap")) || 24;

    var tarjetasVisibles = Math.max(1, Math.floor((anchoVentana + gap) / (anchoTarjeta + gap)));
    var indiceMaximo = Math.max(0, totalTarjetas - tarjetasVisibles);
    var paso = anchoTarjeta + gap;

    return {
      anchoVentana: anchoVentana,
      anchoTarjeta: anchoTarjeta,
      gap: gap,
      tarjetasVisibles: tarjetasVisibles,
      indiceMaximo: indiceMaximo,
      paso: paso
    };
  }

  function crearPuntos() {
    var medidas = calcularMedidas();

    contenedorPuntos.empty();

    for (var i = 0; i <= medidas.indiceMaximo; i++) {
      var punto = $("<button></button>").addClass("punto");

      if (i === indiceActual) {
        punto.addClass("activo");
      }

      contenedorPuntos.append(punto);
    }

    puntos = $(".punto");
  }

  function irA(nuevoIndice, animar) {
    var medidas = calcularMedidas();

    if (nuevoIndice < 0) {
      nuevoIndice = 0;
    }

    if (nuevoIndice > medidas.indiceMaximo) {
      nuevoIndice = medidas.indiceMaximo;
    }

    indiceActual = nuevoIndice;

    var desplazamiento = indiceActual * medidas.paso;

    pista.stop(true, true);

    if (animar === false) {
      pista.css("margin-left", -desplazamiento + "px");
    } else {
      pista.animate(
        { marginLeft: -desplazamiento + "px" },
        400,
        "easeInOutQuad"
      );
    }

    puntos.removeClass("activo");
    puntos.eq(indiceActual).addClass("activo");
  }

  crearPuntos();
  irA(0, false);

  $("#btn-siguiente").on("click", function () {
    irA(indiceActual + 1, true);
  });

  $("#btn-anterior").on("click", function () {
    irA(indiceActual - 1, true);
  });

  contenedorPuntos.on("click", ".punto", function () {
    irA($(this).index(), true);
  });

  $(window).on("resize", function () {
    crearPuntos();
    irA(indiceActual, false);
  });

});


/* -------------------------------------------------------
   4. EFECTO WOW — tarjeta 3D que sigue el ratón
   ------------------------------------------------------- */

var tarjetaHero = document.querySelector(".hero__tarjeta");

if (tarjetaHero) {
  tarjetaHero.addEventListener("mousemove", function (evento) {
    var rect = tarjetaHero.getBoundingClientRect();

    var x = evento.clientX - rect.left;
    var y = evento.clientY - rect.top;

    var porcentajeX = x / rect.width;
    var porcentajeY = y / rect.height;

    var rotacionY = (porcentajeX - 0.5) * 18;
    var rotacionX = (0.5 - porcentajeY) * 18;

    tarjetaHero.style.setProperty("--mouse-x", (porcentajeX * 100) + "%");
    tarjetaHero.style.setProperty("--mouse-y", (porcentajeY * 100) + "%");

    tarjetaHero.style.transform =
      "rotateX(" + rotacionX + "deg) rotateY(" + rotacionY + "deg) translateY(-8px)";
  });

  tarjetaHero.addEventListener("mouseleave", function () {
    tarjetaHero.style.transform = "";
    tarjetaHero.style.setProperty("--mouse-x", "50%");
    tarjetaHero.style.setProperty("--mouse-y", "50%");
  });
}


/* -------------------------------------------------------
   5. SCROLL REVEAL — aparece al hacer scroll
   ------------------------------------------------------- */

var elementosReveal = document.querySelectorAll(".card, .carrusel-tarjeta, .footer__contenido");

elementosReveal.forEach(function (elemento) {
  elemento.classList.add("reveal");
});

var observer = new IntersectionObserver(function (entradas) {
  entradas.forEach(function (entrada) {
    if (entrada.isIntersecting) {
      entrada.target.classList.add("visible");
    }
  });
}, {
  threshold: 0.15
});

elementosReveal.forEach(function (elemento) {
  observer.observe(elemento);
});