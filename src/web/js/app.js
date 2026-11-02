/*
 * Nombre: app.js
 * Descripción: Cliente de la API real para la web temporal de SENDA.
 * Copyright: (c) 2026 Equipo 2 - SENDA / OZORA.
 * Fecha: 02/11/2026
 * Autor: Julia (con asistencia de Codex).
 * Aportación: Acceso, alta, última lectura y recuperación sin datos simulados de interfaz.
 */
'use strict';
let tokenSesion = ''; // Solo en memoria: recargar requiere iniciar sesión otra vez.
let usuarioActual = null;
let tokenRecuperacion = '';

/** Diseño lógico: (id) -> elemento -> nodo DOM.
 * Descripción: Localiza un control conocido de la página. */
function elemento(id) { return document.getElementById(id); }

/** Diseño lógico: (texto, error) -> mensaje -> vacío.
 * Descripción: Informa del resultado con texto seguro y aviso accesible. */
function mensaje(texto, error = false) {
    elemento('mensaje').textContent = texto;
    elemento('mensaje').classList.toggle('error', error);
}

/** Diseño lógico: (sección) -> mostrar -> vacío.
 * Descripción: Cambia de pantalla sin duplicar formularios. */
function mostrar(seccion) {
    for (const id of ['acceso', 'recuperacion', 'restablecer', 'panel']) {
        elemento(id).hidden = id !== seccion;
    }
    document.body.classList.toggle('panel-abierto', seccion === 'panel');
    elemento('navegacion-privada').hidden = seccion !== 'panel' || usuarioActual?.rol !== 'jefe_parque';
    mensaje('');
}

/** Diseño lógico: (ruta, método, datos) -> api -> datos de respuesta o error.
 * Descripción: Envía JSON al mismo origen y exige confirmación real del servidor. */
async function api(ruta, metodo = 'GET', datos = null) {
    const headers = { Accept: 'application/json' };
    if (tokenSesion) headers.Authorization = `Bearer ${tokenSesion}`;
    if (datos !== null) headers['Content-Type'] = 'application/json';
    let respuesta;
    try {
        respuesta = await fetch(`/api/v1${ruta}`, {
            method: metodo, headers, body: datos === null ? null : JSON.stringify(datos),
            cache: 'no-store', credentials: 'omit', signal: AbortSignal.timeout(15000),
        });
    } catch (error) {
        throw new Error('No se ha podido contactar con el servidor. Comprueba la conexión antes de reintentar.');
    }
    let contenido;
    try { contenido = await respuesta.json(); }
    catch (error) { throw new Error('El servidor no ha devuelto una respuesta válida.'); }
    if (respuesta.status === 401 && tokenSesion) {
        tokenSesion = '';
        usuarioActual = null;
        mostrar('acceso');
        elemento('lectura').replaceChildren();
    }
    if (!respuesta.ok || contenido.ok !== true) {
        throw new Error(contenido.error?.mensaje || 'No se ha podido completar la operación.');
    }
    return contenido.data;
}

/** Diseño lógico: (evento, operación) -> ejecutarFormulario -> promesa vacía.
 * Descripción: Evita envíos repetidos y presenta errores sin perder el formulario. */
async function ejecutarFormulario(evento, operacion) {
    evento.preventDefault();
    const boton = evento.currentTarget.querySelector('button[type="submit"]');
    if (boton.disabled) return;
    boton.disabled = true;
    mensaje('');
    try { await operacion(); }
    catch (error) { mensaje(error.message, true); }
    finally { boton.disabled = false; }
}

/** Diseño lógico: (texto, clase) -> parrafo -> nodo p.
 * Descripción: Crea texto de lectura sin interpretar HTML procedente del servidor. */
function parrafo(texto, clase = '') {
    const nodo = document.createElement('p');
    nodo.textContent = texto;
    nodo.className = clase;
    return nodo;
}

/** Diseño lógico: () -> actualizarLectura -> promesa vacía.
 * Descripción: Consulta la captura más reciente y distingue error, ausencia y simulación. */
async function actualizarLectura() {
    const lectura = elemento('lectura');
    const boton = elemento('actualizar');
    boton.disabled = true;
    lectura.replaceChildren(parrafo('Consultando…'));
    try {
        const datos = await api(`/parques/${usuarioActual.id_parque}/mediciones/ultima`);
        if (!datos.medicion) {
            lectura.replaceChildren(parrafo('Todavía no hay mediciones en este parque.'));
            return;
        }
        const m = datos.medicion;
        const valor = parrafo(String(m.valor), 'valor');
        const unidad = document.createElement('span');
        unidad.textContent = ` ${m.unidad} de O₃`;
        valor.append(unidad);
        lectura.replaceChildren(valor);
        if (m.simulada) lectura.append(parrafo('Datos de prueba · Lectura simulada', 'simulada'));
        const lista = document.createElement('dl');
        for (const [nombre, texto] of [
            ['Llavero', m.nombre_micro],
            ['Capturada', new Date(m.fecha_hora).toLocaleString('es-ES')],
            ['Recibida', new Date(m.recibida_en).toLocaleString('es-ES')],
            ['Ubicación', `${m.latitud}, ${m.longitud}`],
        ]) {
            const titulo = document.createElement('dt');
            const contenido = document.createElement('dd');
            titulo.textContent = nombre;
            contenido.textContent = texto;
            lista.append(titulo, contenido);
        }
        lectura.append(lista);
    } catch (error) {
        lectura.replaceChildren(parrafo('No se ha podido consultar la última medición. Pulsa Actualizar para reintentar.'));
        mensaje(error.message, true);
    } finally { boton.disabled = false; }
}

/** Diseño lógico: (evento submit) -> iniciarSesion -> promesa vacía.
 * Descripción: Abre el panel adecuado al rol devuelto por el servidor. */
async function iniciarSesion(evento) {
    await ejecutarFormulario(evento, async function realizarLogin() {
        // Diseño lógico: () -> realizarLogin -> promesa vacía. Descripción: Obtiene y aplica la sesión real.
        const datos = await api('/sesiones', 'POST', {
            correo: elemento('correo').value, contrasena: elemento('contrasena').value,
        });
        tokenSesion = datos.token;
        usuarioActual = datos.usuario;
        elemento('contrasena').value = '';
        mostrar('panel');
        elemento('saludo').textContent = `Hola, ${usuarioActual.nombre}.`;
        elemento('parque').textContent = usuarioActual.id_parque ? `Parque ${usuarioActual.id_parque}` : '';
        const jefe = usuarioActual.rol === 'jefe_parque';
        elemento('contenido-jefe').hidden = !jefe;
        elemento('contenido-trabajador').hidden = jefe;
        if (jefe) await actualizarLectura();
    });
}

/** Diseño lógico: (evento submit) -> registrarTrabajador -> promesa vacía.
 * Descripción: Crea la cuenta del parque y comunica que el enlace está en cola. */
async function registrarTrabajador(evento) {
    await ejecutarFormulario(evento, async function realizarAlta() {
        // Diseño lógico: () -> realizarAlta -> promesa vacía. Descripción: Envía los datos del alta al servidor.
        await api('/trabajadores', 'POST', { nombre: elemento('nombre-trabajador').value, correo: elemento('correo-trabajador').value });
        elemento('form-alta').reset();
        mensaje('Cuenta creada. El enlace para establecer la contraseña está pendiente de entrega por correo.');
    });
}

/** Diseño lógico: (evento submit) -> solicitarRecuperacion -> promesa vacía.
 * Descripción: Solicita un enlace sin revelar cuentas registradas. */
async function solicitarRecuperacion(evento) {
    await ejecutarFormulario(evento, async function realizarSolicitud() {
        // Diseño lógico: () -> realizarSolicitud -> promesa vacía. Descripción: Presenta la respuesta genérica de recuperación.
        const datos = await api('/recuperaciones', 'POST', { correo: elemento('correo-recuperacion').value });
        mensaje(datos.mensaje);
    });
}

/** Diseño lógico: (evento submit) -> restablecerContrasena -> promesa vacía.
 * Descripción: Comprueba la repetición y consume el enlace de alta o recuperación. */
async function restablecerContrasena(evento) {
    await ejecutarFormulario(evento, async function realizarCambio() {
        // Diseño lógico: () -> realizarCambio -> promesa vacía. Descripción: Cambia la contraseña y vuelve al acceso.
        const nueva = elemento('nueva').value;
        if (nueva !== elemento('repetir').value) throw new Error('Las contraseñas no coinciden.');
        const longitud = new TextEncoder().encode(nueva).length;
        if (longitud < 12 || longitud > 72) throw new Error('La contraseña debe ocupar entre 12 y 72 bytes UTF-8.');
        await api('/recuperaciones/confirmacion', 'POST', { token: tokenRecuperacion, contrasena_nueva: nueva });
        tokenRecuperacion = '';
        tokenSesion = '';
        usuarioActual = null;
        elemento('form-restablecer').reset();
        mostrar('acceso');
        mensaje('Contraseña actualizada. Ya puedes iniciar sesión con ella.');
    });
}

/** Diseño lógico: () -> cerrarSesion -> promesa vacía.
 * Descripción: Revoca la sesión remota y conserva la posibilidad de reintentar si falla. */
async function cerrarSesion() {
    elemento('salir').disabled = true;
    try {
        await api('/sesiones/actual', 'DELETE');
        tokenSesion = '';
        usuarioActual = null;
        elemento('lectura').replaceChildren();
        elemento('form-alta').reset();
        mostrar('acceso');
        mensaje('Sesión cerrada.');
    } catch (error) { mensaje(error.message, true); }
    finally { elemento('salir').disabled = false; }
}

/** Diseño lógico: () -> abrirRecuperacion -> vacío.
 * Descripción: Muestra el formulario de solicitud de enlace. */
function abrirRecuperacion() { mostrar('recuperacion'); }
/** Diseño lógico: () -> volverAcceso -> vacío.
 * Descripción: Descarta el enlace local y limpia los campos de contraseña. */
function volverAcceso() { tokenRecuperacion = ''; elemento('form-restablecer').reset(); mostrar('acceso'); }

elemento('form-login').addEventListener('submit', iniciarSesion);
elemento('form-alta').addEventListener('submit', registrarTrabajador);
elemento('form-recuperacion').addEventListener('submit', solicitarRecuperacion);
elemento('form-restablecer').addEventListener('submit', restablecerContrasena);
elemento('abrir-recuperacion').addEventListener('click', abrirRecuperacion);
elemento('volver-acceso').addEventListener('click', volverAcceso);
elemento('cancelar-restablecer').addEventListener('click', volverAcceso);
elemento('actualizar').addEventListener('click', actualizarLectura);
elemento('salir').addEventListener('click', cerrarSesion);

const fragmento = new URLSearchParams(location.hash.slice(1));
if (fragmento.has('token')) {
    tokenRecuperacion = fragmento.get('token');
    history.replaceState(null, '', location.pathname);
    mostrar('restablecer');
}
