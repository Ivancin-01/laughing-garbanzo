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