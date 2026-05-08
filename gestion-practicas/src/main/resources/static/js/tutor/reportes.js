document.addEventListener("DOMContentLoaded", function () {
    console.log("✅ reportes.js tutor cargado");

    inicializarNavbar();
    inicializarContadorComentario();
});

function inicializarNavbar() {
    const btnSidebar = document.getElementById("btn-toggle-sidebar");
    const sidebar = document.querySelector(".sidebar");

    const perfilTrigger = document.querySelector(".perfil-trigger");
    const perfilBoton = document.getElementById("perfil-boton");
    const dropdown = document.getElementById("dropdown-perfil");

    if (btnSidebar && sidebar) {
        btnSidebar.addEventListener("click", function () {
            sidebar.classList.toggle("oculto");
        });
    }

    if (perfilBoton && dropdown && perfilTrigger) {
        perfilBoton.addEventListener("click", function (event) {
            event.preventDefault();
            event.stopPropagation();

            dropdown.classList.toggle("show");
            perfilTrigger.classList.toggle("dropdown-abierto");
        });

        window.addEventListener("click", function (event) {
            if (!perfilTrigger.contains(event.target)) {
                dropdown.classList.remove("show");
                perfilTrigger.classList.remove("dropdown-abierto");
            }
        });

        window.addEventListener("keydown", function (event) {
            if (event.key === "Escape") {
                dropdown.classList.remove("show");
                perfilTrigger.classList.remove("dropdown-abierto");
            }
        });
    }
}

function inicializarContadorComentario() {
    const textarea = document.getElementById("comentario");
    const contador = document.getElementById("contador-comentario");

    if (!textarea || !contador) {
        return;
    }

    const actualizarContador = function () {
        contador.textContent = textarea.value.length;
    };

    textarea.addEventListener("input", actualizarContador);
    actualizarContador();
}