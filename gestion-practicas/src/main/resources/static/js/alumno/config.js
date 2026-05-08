document.addEventListener("DOMContentLoaded", function () {
    console.log("✅ config.js alumno cargado");

    const perfilTrigger = document.querySelector(".perfil-trigger");
    const perfilBoton = document.getElementById("perfil-boton");
    const dropdown = document.getElementById("dropdown-perfil");

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

    const formCritico = document.querySelector(".zona-critica form");

    if (formCritico) {
        formCritico.addEventListener("submit", function (event) {
            const confirmar = confirm("¿Seguro que quieres desactivar tu cuenta?");
            if (!confirmar) {
                event.preventDefault();
            }
        });
    }
});