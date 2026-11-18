/*
 * Nombre: publica.js
 * Descripción: Solicitudes de información y compatibilidad con enlaces de recuperación antiguos.
 * Copyright: (c) 2026 Equipo 2 - SENDA / OZORA.
 * Fecha: 02/11/2026
 * Autor: Julia (con asistencia de Codex).
 * Aportación: Formulario público real, sin confirmaciones simuladas ni persistencia en navegador.
 */
'use strict';
const RUTA_BASE = document.documentElement.dataset.basePath || '';
const logica = new LogicaFake(RUTA_BASE);
const formularioContacto = document.getElementById('form-contacto');
const resultadoContacto = document.getElementById('resultado-contacto');

/** Diseño lógico: (evento submit) -> solicitarInformacion -> promesa vacía.
 * Descripción: Guarda la solicitud en el servidor y solo confirma cuando este la acepta. */
async function solicitarInformacion(evento) {
    evento.preventDefault();
    const boton = formularioContacto.querySelector('button[type="submit"]');
    if (boton.disabled) return;
    boton.disabled = true;
    resultadoContacto.textContent = '';
    resultadoContacto.classList.remove('error');
    try {
        const datos = Object.fromEntries(new FormData(formularioContacto));
        const contenido = await logica.registrarSolicitud(datos);
        resultadoContacto.textContent = contenido.mensaje;
        formularioContacto.reset();
    } catch (error) {
        resultadoContacto.classList.add('error');
        resultadoContacto.textContent = error instanceof TypeError || error.name === 'TimeoutError'
            ? 'No hemos podido confirmar la recepción. Comprueba la conexión antes de reintentar.' : error.message;
    } finally { boton.disabled = false; }
}
formularioContacto.addEventListener('submit', solicitarInformacion);

// Los correos emitidos antes de separar la landing enlazaban a /#token=... .
const enlaceAntiguo = new URLSearchParams(location.hash.slice(1));
if (enlaceAntiguo.has('token')) location.replace(RUTA_BASE + '/acceso' + location.hash);
