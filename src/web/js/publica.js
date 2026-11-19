/*
 * Nombre: publica.js
 * Descripción: Presentación pública OZORA y panel del parque.
 * Copyright: (c) 2026 Equipo 2 - SENDA / OZORA.
 * Fecha: 02/11/2026
 * Autores: Fabián Andrés Useche Rivera; Élia Garcia Pons; Judith Civico Aporta; Julia Valen de Oliveira; Soufiane Belfquih El Khalfi
 * Aportación: Presentación pública OZORA y panel del parque.
 */
'use strict';
const RUTA_BASE = document.documentElement.dataset.basePath || '';
const logica = new LogicaFake(RUTA_BASE);
const formularioContacto = document.getElementById('form-contacto');
const resultadoContacto = document.getElementById('resultado-contacto');



/** --------------------
 * Diseño lógico: evento: Dato --> solicitarInformacion() -->
 * Dato <--
 * Descripción: Guarda la solicitud en el servidor y solo confirma cuando este la acepta
 * -------------------- */
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

/** --------------------
 * Diseño lógico: pestaña: Elemento --> seleccionarPestana() -->
 * Descripción: Muestra el llavero, la app o la web de SENDA y mueve el indicador de puntos.
 * Accesible con teclado (flechas izquierda/derecha) según el patrón ARIA de pestañas.
 * -------------------- */
const pestanas = [...document.querySelectorAll('.pestanas [role="tab"]')];
const puntos = [...document.querySelectorAll('.puntos i')];
function seleccionarPestana(pestana, enfocar) {
    pestanas.forEach((otra, indice) => {
        const activa = otra === pestana;
        otra.setAttribute('aria-selected', String(activa));
        otra.tabIndex = activa ? 0 : -1;
        document.getElementById(otra.getAttribute('aria-controls')).hidden = !activa;
        puntos[indice]?.classList.toggle('activo', activa);
    });
    if (enfocar) pestana.focus();
}
pestanas.forEach((pestana, indice) => {
    pestana.addEventListener('click', () => seleccionarPestana(pestana, false));
    pestana.addEventListener('keydown', evento => {
        if (evento.key !== 'ArrowRight' && evento.key !== 'ArrowLeft') return;
        const paso = evento.key === 'ArrowRight' ? 1 : -1;
        seleccionarPestana(pestanas[(indice + paso + pestanas.length) % pestanas.length], true);
    });
});

// Los correos emitidos antes de separar la landing enlazaban a /#token=... .
const enlaceAntiguo = new URLSearchParams(location.hash.slice(1));
if (enlaceAntiguo.has('token')) location.replace(RUTA_BASE + '/acceso' + location.hash);
