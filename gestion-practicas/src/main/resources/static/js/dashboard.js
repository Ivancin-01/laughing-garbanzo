/* =====================================================
   FP Connect — dashboard.js
   Navegación SPA, sidebar, notificaciones, chat,
   dropzone y botones interactivos.
   ===================================================== */

/* -------------------------------------------------------
   1. NAVEGACIÓN ENTRE SECCIONES (SPA sin recarga)
   ------------------------------------------------------- */

/**
 * Muestra la sección indicada y marca el enlace activo.
 * @param {string} idSeccion - Nombre de la sección (sin el prefijo "sec-")
 */
function mostrarSeccion(idSeccion) {
  // Ocultamos todas las secciones
  document.querySelectorAll('.seccion').forEach(function (sec) {
    sec.classList.remove('activa');
  });

  // Desactivamos todos los enlaces del sidebar
  document.querySelectorAll('.sidebar__link').forEach(function (enlace) {
    enlace.classList.remove('activo');
  });

  // Mostramos la sección objetivo
  var seccionObjetivo = document.getElementById('sec-' + idSeccion);
  if (seccionObjetivo) {
    seccionObjetivo.classList.add('activa');
  }

  // Marcamos el enlace del sidebar correspondiente
  var enlaceActivo = document.querySelector('.sidebar__link[data-seccion="' + idSeccion + '"]');
  if (enlaceActivo) {
    enlaceActivo.classList.add('activo');
  }
}

// Delegamos los clics en todos los enlaces con data-seccion
document.addEventListener('click', function (e) {
  var enlace = e.target.closest('[data-seccion]');
  if (enlace) {
    e.preventDefault();
    var seccion = enlace.getAttribute('data-seccion');
    mostrarSeccion(seccion);
    // Cerramos paneles abiertos al navegar
    cerrarTodosLosPaneles();
  }
});


/* -------------------------------------------------------
   2. SIDEBAR — Colapsar / expandir
   ------------------------------------------------------- */
var sidebar        = document.getElementById('sidebar');
var contenido      = document.getElementById('contenido-principal');
var btnToggle      = document.getElementById('sidebar-toggle');

if (btnToggle) {
  btnToggle.addEventListener('click', function () {
    sidebar.classList.toggle('colapsado');
    document.body.classList.toggle('sidebar-colapsado');
  });
}


/* -------------------------------------------------------
   3. PANEL DE NOTIFICACIONES
   ------------------------------------------------------- */
var btnNotif         = document.getElementById('btn-notificaciones');
var panelNotif       = document.getElementById('notificaciones-panel');
var badgeNotif       = document.getElementById('badge-notif');
var btnMarcarLeidas  = document.getElementById('marcar-leidas');
var overlay          = document.getElementById('overlay');

function abrirNotificaciones() {
  panelNotif.classList.add('visible');
  overlay.classList.add('visible');
}

function cerrarNotificaciones() {
  panelNotif.classList.remove('visible');
  overlay.classList.remove('visible');
}

function cerrarTodosLosPaneles() {
  cerrarNotificaciones();
  cerrarMenuUsuario();
}

if (btnNotif) {
  btnNotif.addEventListener('click', function (e) {
    e.stopPropagation();
    var estaAbierto = panelNotif.classList.contains('visible');
    cerrarTodosLosPaneles();
    if (!estaAbierto) {
      abrirNotificaciones();
    }
  });
}

// Marcar todas las notificaciones como leídas
if (btnMarcarLeidas) {
  btnMarcarLeidas.addEventListener('click', function () {
    document.querySelectorAll('.notif-item--nueva').forEach(function (item) {
      item.classList.remove('notif-item--nueva');
    });
    // Ocultamos el badge
    if (badgeNotif) {
      badgeNotif.style.display = 'none';
    }
  });
}

// Cerrar al hacer clic en el overlay
if (overlay) {
  overlay.addEventListener('click', function () {
    cerrarTodosLosPaneles();
  });
}


/* -------------------------------------------------------
   4. MENÚ DESPLEGABLE DEL USUARIO (topbar)
   ------------------------------------------------------- */
var btnMenuUsuario   = document.getElementById('btn-menu-usuario');
var dropdownUsuario  = document.getElementById('dropdown-usuario');

function abrirMenuUsuario() {
  dropdownUsuario.classList.add('visible');
  btnMenuUsuario.classList.add('abierto');
  overlay.classList.add('visible');
}

function cerrarMenuUsuario() {
  if (dropdownUsuario) {
    dropdownUsuario.classList.remove('visible');
  }
  if (btnMenuUsuario) {
    btnMenuUsuario.classList.remove('abierto');
  }
  overlay.classList.remove('visible');
}

if (btnMenuUsuario) {
  btnMenuUsuario.addEventListener('click', function (e) {
    e.stopPropagation();
    var estaAbierto = dropdownUsuario.classList.contains('visible');
    cerrarTodosLosPaneles();
    if (!estaAbierto) {
      abrirMenuUsuario();
    }
  });
}


/* -------------------------------------------------------
   5. CHAT — enviar mensaje
   ------------------------------------------------------- */
var chatInput     = document.getElementById('chat-input');
var btnEnviar     = document.getElementById('btn-enviar');
var chatMensajes  = document.getElementById('chat-mensajes');

/**
 * Crea y añade un nuevo mensaje enviado al chat.
 */
function enviarMensaje() {
  if (!chatInput) return;

  var texto = chatInput.value.trim();
  if (!texto) return;

  // Creamos el elemento de mensaje
  var ahora = new Date();
  var hora  = ahora.getHours().toString().padStart(2, '0') + ':' +
              ahora.getMinutes().toString().padStart(2, '0');

  var mensajeEl = document.createElement('div');
  mensajeEl.className = 'mensaje mensaje--enviado';
  mensajeEl.innerHTML =
    '<div class="mensaje__burbuja">' + escapeHTML(texto) + '</div>' +
    '<span class="mensaje__hora">Hoy, ' + hora + '</span>';

  chatMensajes.appendChild(mensajeEl);

  // Hacemos scroll al final
  chatMensajes.scrollTop = chatMensajes.scrollHeight;

  // Limpiamos el input
  chatInput.value = '';
  chatInput.focus();
}

if (btnEnviar) {
  btnEnviar.addEventListener('click', enviarMensaje);
}

if (chatInput) {
  chatInput.addEventListener('keydown', function (e) {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      enviarMensaje();
    }
  });
}

// Cambiar conversación activa en la lista
document.querySelectorAll('.conversacion').forEach(function (conv) {
  conv.addEventListener('click', function () {
    document.querySelectorAll('.conversacion').forEach(function (c) {
      c.classList.remove('activa');
    });
    conv.classList.add('activa');

    // Eliminamos el badge de mensajes no leídos de esa conversación
    var badge = conv.querySelector('.conv-badge');
    if (badge) badge.remove();
  });
});


/* -------------------------------------------------------
   6. BOTONES "SOLICITAR" en ofertas
   ------------------------------------------------------- */

/**
 * Marca una oferta como solicitada tras confirmar.
 */
document.addEventListener('click', function (e) {
  var btn = e.target.closest('.btn-solicitar');
  if (!btn) return;

  if (btn.getAttribute('data-estado') === 'solicitada') return; // Ya solicitada

  // Confirmación visual simple
  btn.textContent = '✔ Solicitada';
  btn.setAttribute('data-estado', 'solicitada');
  btn.style.transition = 'all 0.3s';
});


/* -------------------------------------------------------
   7. DROPZONE — documentos (drag & drop visual)
   ------------------------------------------------------- */
var dropzone   = document.getElementById('dropzone');
var inputFile  = document.getElementById('input-archivo');
var btnSubir   = document.getElementById('btn-subir-doc');

if (dropzone) {
  // Clic en la zona abre el selector de archivo
  dropzone.addEventListener('click', function () {
    if (inputFile) inputFile.click();
  });

  // Drag over
  dropzone.addEventListener('dragover', function (e) {
    e.preventDefault();
    dropzone.classList.add('drag-sobre');
  });

  // Drag leave
  dropzone.addEventListener('dragleave', function () {
    dropzone.classList.remove('drag-sobre');
  });

  // Drop
  dropzone.addEventListener('drop', function (e) {
    e.preventDefault();
    dropzone.classList.remove('drag-sobre');
    var archivos = e.dataTransfer.files;
    if (archivos.length > 0) {
      procesarArchivo(archivos[0]);
    }
  });
}

// Botón "Subir documento" de la cabecera también abre el selector
if (btnSubir && inputFile) {
  btnSubir.addEventListener('click', function () {
    inputFile.click();
  });
}

// Cuando se selecciona un archivo con el input
if (inputFile) {
  inputFile.addEventListener('change', function () {
    if (inputFile.files.length > 0) {
      procesarArchivo(inputFile.files[0]);
    }
  });
}

/**
 * Añade visualmente el archivo subido a la lista de documentos.
 * En producción, aquí iría la llamada AJAX al endpoint de subida.
 * @param {File} archivo
 */
function procesarArchivo(archivo) {
  var docsGrid = document.querySelector('.docs-grid');
  if (!docsGrid) return;

  // Formateamos el tamaño
  var tamañoKB = (archivo.size / 1024).toFixed(0);

  var ahora  = new Date();
  var fecha  = ahora.getDate() + ' ' +
               ['ene','feb','mar','abr','may','jun','jul','ago','sep','oct','nov','dic'][ahora.getMonth()] +
               ' ' + ahora.getFullYear();

  // Creamos la tarjeta de documento
  var docCard = document.createElement('div');
  docCard.className = 'doc-card';
  docCard.innerHTML =
    '<div class="doc-card__icono">📄</div>' +
    '<div class="doc-card__info">' +
      '<p class="doc-card__nombre">' + escapeHTML(archivo.name) + '</p>' +
      '<p class="doc-card__meta">Subido el ' + fecha + ' · ' + tamañoKB + ' KB</p>' +
    '</div>' +
    '<div class="doc-card__acciones">' +
      '<button class="btn-doc" title="Descargar">⬇</button>' +
      '<button class="btn-doc btn-doc--eliminar" title="Eliminar">🗑</button>' +
    '</div>';

  // Insertamos al principio de la lista
  docsGrid.insertBefore(docCard, docsGrid.firstChild);

  // Pequeña animación de entrada
  docCard.style.opacity = '0';
  docCard.style.transform = 'translateY(-8px)';
  docCard.style.transition = 'opacity 0.3s, transform 0.3s';
  requestAnimationFrame(function () {
    docCard.style.opacity = '1';
    docCard.style.transform = 'translateY(0)';
  });
}

// Delegamos el clic en botones eliminar de documentos
document.addEventListener('click', function (e) {
  if (e.target.closest('.btn-doc--eliminar')) {
    var card = e.target.closest('.doc-card');
    if (card) {
      card.style.transition = 'opacity 0.2s, transform 0.2s';
      card.style.opacity = '0';
      card.style.transform = 'translateX(16px)';
      setTimeout(function () { card.remove(); }, 220);
    }
  }
});


/* -------------------------------------------------------
   8. FILTROS DE OFERTAS (búsqueda en tiempo real)
   ------------------------------------------------------- */
var filtroBusqueda = document.getElementById('filtro-busqueda');
var filtroCiclo    = document.getElementById('filtro-ciclo');
var filtroModal    = document.getElementById('filtro-modalidad');

function aplicarFiltros() {
  var textoBusqueda = filtroBusqueda ? filtroBusqueda.value.toLowerCase() : '';
  var cicloBuscado  = filtroCiclo    ? filtroCiclo.value.toLowerCase()    : '';

  document.querySelectorAll('#lista-ofertas .oferta-card').forEach(function (card) {
    var titulo   = (card.querySelector('.oferta-card__titulo')?.textContent  || '').toLowerCase();
    var empresa  = (card.querySelector('.oferta-card__empresa')?.textContent || '').toLowerCase();
    var cicloTag = (card.querySelector('.oferta-tag')?.textContent           || '').toLowerCase();

    var pasaTexto = !textoBusqueda || titulo.includes(textoBusqueda) || empresa.includes(textoBusqueda);
    var pasaCiclo = !cicloBuscado  || cicloTag.includes(cicloBuscado);

    card.style.display = (pasaTexto && pasaCiclo) ? '' : 'none';
  });
}

if (filtroBusqueda) filtroBusqueda.addEventListener('input', aplicarFiltros);
if (filtroCiclo)    filtroCiclo.addEventListener('change', aplicarFiltros);
if (filtroModal)    filtroModal.addEventListener('change', aplicarFiltros);


/* -------------------------------------------------------
   9. GUARDAR CAMBIOS EN EL PERFIL (simulado)
   ------------------------------------------------------- */
document.addEventListener('click', function (e) {
  var btnGuardar = e.target.closest('#sec-perfil .btn-principal');
  if (!btnGuardar) return;

  var textoOriginal = btnGuardar.textContent;
  btnGuardar.textContent = '✔ Guardado';
  btnGuardar.disabled = true;
  btnGuardar.style.background = 'var(--verde-claro)';

  setTimeout(function () {
    btnGuardar.textContent = textoOriginal;
    btnGuardar.disabled = false;
    btnGuardar.style.background = '';
  }, 2500);
});


/* -------------------------------------------------------
   UTILIDAD: Escape de HTML para evitar XSS
   ------------------------------------------------------- */
function escapeHTML(str) {
  var div = document.createElement('div');
  div.appendChild(document.createTextNode(str));
  return div.innerHTML;
}


/* -------------------------------------------------------
   INIT — Estado inicial al cargar la página
   ------------------------------------------------------- */
(function init() {
  // Aseguramos que la sección "inicio" sea la activa al arrancar
  mostrarSeccion('inicio');
})();
