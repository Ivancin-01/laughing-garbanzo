document.addEventListener('DOMContentLoaded', function() {
    
    // Función para el Dropdown de la Navbar
    window.toggleDropdown = function() {
        const dropdown = document.getElementById("dropdown-perfil");
        if (dropdown) {
            dropdown.classList.toggle("show");
        }
    };

    // Cerrar el dropdown al hacer clic fuera
    window.addEventListener('click', function(event) {
        if (!event.target.closest('.perfil-trigger')) {
            const dropdown = document.getElementById("dropdown-perfil");
            if (dropdown && dropdown.classList.contains('show')) {
                dropdown.classList.remove('show');
            }
        }
    });

    // Log para confirmar que carga bien
    console.log("JS de Perfil cargado correctamente");
});

// Función para previsualizar la imagen antes de subirla
function previewImage(input) {
    if (input.files && input.files[0]) {
        var reader = new FileReader();

        reader.onload = function(e) {
            // 1. Actualizamos la foto grande del perfil
            const fotoGrande = document.getElementById('foto-previsualizacion');
            if (fotoGrande) {
                fotoGrande.src = e.target.result;
            }

            // 2. Opcional: Actualizamos también la foto pequeña de la navbar superior
            const fotoNav = document.querySelector('.avatar-nav');
            if (fotoNav) {
                fotoNav.src = e.target.result;
            }
        }

        // Leemos el archivo como una URL de datos
        reader.readAsDataURL(input.files[0]);
    }
}

document.addEventListener("DOMContentLoaded", function() {
    const selectCiudad = document.getElementById('select-ciudad');
    const ciudadGuardada = selectCiudad.getAttribute('data-actual') || "";

    fetch('/json/municipios.json').then(response => response.json()).then(data => {
        selectCiudad.innerHTML = '<option value="">Selecciona una ciudad...</option>';

        data.forEach(ciudad => {
            const option = document.createElement('option');
            option.value = ciudad;
            option.textContent = ciudad;

            if (ciudad === ciudadGuardada) {
                option.selected = true;
            }
            selectCiudad.appendChild(option);
        });
    }).catch(error => console.error('Error cargando municipios:' + error));
}); 