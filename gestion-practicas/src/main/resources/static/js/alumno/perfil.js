document.addEventListener("DOMContentLoaded", function () {
    console.log("✅ perfil.js alumno cargado");

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

    cargarMunicipios();
    inicializarZonaCV();
});

function cargarMunicipios() {
    const selectCiudad = document.getElementById("select-ciudad");

    if (!selectCiudad) {
        return;
    }

    const ciudadGuardada = selectCiudad.getAttribute("data-actual") || "";

    fetch("/json/municipios.json")
        .then(response => response.json())
        .then(data => {
            selectCiudad.innerHTML = '<option value="">Selecciona una ciudad...</option>';

            data.forEach(ciudad => {
                const option = document.createElement("option");
                option.value = ciudad;
                option.textContent = ciudad;

                if (ciudad === ciudadGuardada) {
                    option.selected = true;
                }

                selectCiudad.appendChild(option);
            });
        })
        .catch(error => console.error("Error cargando municipios:", error));
}

function inicializarZonaCV() {
    const zonaCv = document.getElementById("zona-cv");
    const inputCv = document.getElementById("input-cv");
    const nombreCv = document.getElementById("nombre-cv");

    if (!zonaCv || !inputCv || !nombreCv) {
        return;
    }

    zonaCv.addEventListener("click", function () {
        inputCv.click();
    });

    inputCv.addEventListener("change", function () {
        mostrarNombreArchivo(inputCv, nombreCv);
    });

    if (window.jQuery && $("#zona-cv").droppable) {
        $("#zona-cv").droppable({
            tolerance: "pointer",

            over: function () {
                $(this).addClass("zona-cv--activa");
            },

            out: function () {
                $(this).removeClass("zona-cv--activa");
            },

            drop: function (event, ui) {
                $(this).removeClass("zona-cv--activa");
            }
        });
    }

    zonaCv.addEventListener("dragover", function (event) {
        event.preventDefault();
        zonaCv.classList.add("zona-cv--activa");
    });

    zonaCv.addEventListener("dragleave", function () {
        zonaCv.classList.remove("zona-cv--activa");
    });

    zonaCv.addEventListener("drop", function (event) {
        event.preventDefault();
        zonaCv.classList.remove("zona-cv--activa");

        const archivos = event.dataTransfer.files;

        if (archivos.length > 0) {
            inputCv.files = archivos;
            mostrarNombreArchivo(inputCv, nombreCv);
        }
    });
}

function mostrarNombreArchivo(inputCv, nombreCv) {
    const archivo = inputCv.files[0];

    if (!archivo) {
        nombreCv.textContent = "Ningún archivo seleccionado";
        return;
    }

    const extensionesPermitidas = ["pdf", "doc", "docx"];
    const extension = archivo.name.split(".").pop().toLowerCase();

    if (!extensionesPermitidas.includes(extension)) {
        nombreCv.textContent = "Formato no válido. Usa PDF, DOC o DOCX.";
        inputCv.value = "";
        return;
    }

    nombreCv.textContent = archivo.name;
}