document.addEventListener('DOMContentLoaded', function() {

    // Toggle del sidebar
    window.toggleSidebar = function() {
        const sidebar = document.querySelector('.sidebar');
        if (sidebar) sidebar.classList.toggle('oculto');
    };

    // Toggle del dropdown del perfil
    window.toggleDropdown = function() {
        const dropdown = document.getElementById('dropdown-perfil');
        if (dropdown) dropdown.classList.toggle('show');
    };

    // Cerrar dropdown al hacer clic fuera
    window.addEventListener('click', function(event) {
        if (!event.target.closest('.perfil-trigger')) {
            const dropdown = document.getElementById('dropdown-perfil');
            if (dropdown && dropdown.classList.contains('show')) {
                dropdown.classList.remove('show');
            }
        }
    });
});