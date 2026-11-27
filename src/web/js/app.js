/*
 * Nombre: app.js
 * Descripción: Presentación pública OZORA y panel del parque.
 * Copyright: (c) 2026 Equipo 2 - SENDA / OZORA.
 * Fecha: 02/11/2026
 * Autores: Fabián Andrés Useche Rivera; Élia Garcia Pons; Judith Civico Aporta; Julia Valen de Oliveira; Soufiane Belfquih El Khalfi
 * Aportación: Presentación pública OZORA y panel del parque.
 */
'use strict';
const logica = new LogicaFake(document.documentElement.dataset.basePath || '');
let tokenSesion = ''; // Solo en memoria: recargar requiere iniciar sesión otra vez.
let usuarioActual = null;
let tokenRecuperacion = '';



/** --------------------
 * Diseño lógico: id: Dato --> elemento() -->
 * Dato <--
 * Descripción: Localiza un control conocido de la página
 * -------------------- */
function elemento(id) { return document.getElementById(id); }



/** --------------------
 * Diseño lógico: texto: Text, error: Dato, destino: Dato --> mensaje() -->
 * Dato <--
 * Descripción: Informa del resultado con texto seguro y aviso accesible
 * -------------------- */
function mensaje(texto, error = false, destino = 'mensaje') {
    const aviso = elemento(destino);
    aviso.textContent = texto;
    aviso.classList.toggle('error', error);
    aviso.hidden = texto === '';
    if (texto && destino !== 'mensaje') {
        aviso.focus({ preventScroll: true });
        aviso.scrollIntoView({ block: 'nearest', behavior: 'smooth' });
    }
}



/** --------------------
 * Diseño lógico: seccion: Dato --> mostrar() -->
 * Dato <--
 * Descripción: Cambia de pantalla sin duplicar formularios
 * -------------------- */
/** --------------------
 * Diseño lógico: valor: Text, correo: Text --> problemaContrasena() -->
 * Text | null <--
 * Descripción: Mismas reglas de contraseña segura que el servidor (Apoyo.php::validarContrasena)
 * -------------------- */
function problemaContrasena(valor, correo) {
    const bytes = new TextEncoder().encode(valor).length;
    if (bytes < 12 || bytes > 72 || valor.includes('\0')) return 'La contraseña debe tener entre 12 y 72 caracteres.';
    const faltan = [];
    if (!/\p{Lu}/u.test(valor)) faltan.push('una mayúscula');
    if (!/\p{Ll}/u.test(valor)) faltan.push('una minúscula');
    if (!/\d/.test(valor)) faltan.push('un número');
    if (!/[^\p{L}\d]/u.test(valor)) faltan.push('un símbolo (! ? # . -)');
    if (faltan.length) return `La contraseña no es segura: le falta ${faltan.join(', ')}.`;
    const minusculas = valor.toLowerCase();
    const partes = (correo.split('@')[0] || '').toLowerCase().split(/[._+\-\d]+/);
    if (partes.some(parte => parte.length >= 4 && minusculas.includes(parte)) || /(.)\1{3,}/u.test(valor)
        || /(1234|2345|3456|4567|5678|6789|abcd|qwer|asdf|password|contrase|senda|ozora)/u.test(minusculas)) {
        return 'La contraseña es fácil de adivinar: evita tu correo, el nombre de la app, secuencias (1234, abcd) o caracteres repetidos.';
    }
    return null;
}

function mostrar(seccion) {
    // Al cambiar de pantalla, todas las claves vuelven a quedar ocultas.
    ocultarContrasenas();
    for (const aviso of document.querySelectorAll('.aviso-formulario')) {
        aviso.textContent = '';
        aviso.hidden = true;
    }
    for (const id of ['acceso', 'recuperacion', 'restablecer', 'cambio-inicial', 'panel']) {
        elemento(id).hidden = id !== seccion;
    }
    document.body.classList.toggle('panel-abierto', seccion === 'panel');
    elemento('navegacion-privada').hidden = seccion !== 'panel' || !['jefe_parque', 'organizacion', 'proveedor'].includes(usuarioActual?.rol);
    mensaje('');
}



/** --------------------
 * Diseño lógico: contenedor: Dato --> ocultarContrasenas() -->
 * Dato <--
 * Descripción: Restablece la privacidad de los campos al navegar o terminar un alta
 * -------------------- */
function ocultarContrasenas(contenedor = document) {
    for (const boton of contenedor.querySelectorAll('[data-ver-contrasena]')) {
        elemento(boton.getAttribute('aria-controls')).type = 'password';
        boton.setAttribute('aria-label', 'Mostrar contraseña');
        boton.title = 'Mostrar contraseña';
        boton.setAttribute('aria-pressed', 'false');
    }
}

/** Aplica la caducidad de sesión a las operaciones del proxy. */

/** --------------------
 * Diseño lógico: operacion: Dato --> conSesion() -->
 * Dato <--
 * Descripción: Con Sesion. Presentación pública OZORA y panel del parque
 * -------------------- */
async function conSesion(operacion) {
    try {
        return await operacion();
    } catch (error) {
        if (error.status === 401 && tokenSesion) {
            tokenSesion = '';
            usuarioActual = null;
            mostrar('acceso');
            elemento('lectura').replaceChildren();
        }
        throw error;
    }
}



/** --------------------
 * Diseño lógico: evento: Dato, operacion: Dato --> ejecutarFormulario() -->
 * Dato <--
 * Descripción: Evita envíos repetidos y presenta errores sin perder el formulario
 * -------------------- */
async function ejecutarFormulario(evento, operacion) {
    evento.preventDefault();
    const formulario = evento.currentTarget;
    const boton = formulario.querySelector('button[type="submit"]');
    const destino = `aviso-${formulario.id}`;
    if (boton.disabled) return;
    boton.disabled = true;
    for (const campo of formulario.querySelectorAll('[aria-invalid]')) {
        campo.removeAttribute('aria-invalid');
        campo.removeAttribute('aria-describedby');
    }
    mensaje('', false, destino);
    try { await operacion(); }
    catch (error) {
        // Si la sesión caduca, el aviso debe aparecer en el acceso que acaba de abrirse.
        const receptor = formulario.closest('[hidden]') ? 'aviso-form-login' : destino;
        for (const campo of formulario.querySelectorAll('input')) {
            if (error.campos?.[campo.name]) {
                campo.setAttribute('aria-invalid', 'true');
                campo.setAttribute('aria-describedby', receptor);
            }
        }
        mensaje(error.message, true, receptor);
    }
    finally { boton.disabled = false; }
}



/** --------------------
 * Diseño lógico: texto: Text, clase: Dato --> parrafo() -->
 * Dato <--
 * Descripción: Crea texto de lectura sin interpretar HTML procedente del servidor
 * -------------------- */
function parrafo(texto, clase = '') {
    const nodo = document.createElement('p');
    nodo.textContent = texto;
    nodo.className = clase;
    return nodo;
}



/** --------------------
 * Diseño lógico: actualizarLectura() -->
 * Dato <--
 * Descripción: Consulta la captura más reciente y distingue error, ausencia y simulación
 * -------------------- */
async function actualizarLectura() {
    const lectura = elemento('lectura');
    const boton = elemento('actualizar');
    boton.disabled = true;
    lectura.replaceChildren(parrafo('Consultando…'));
    try {
        const datos = await conSesion(() => logica.ultimaMedicion(tokenSesion, usuarioActual.id_parque));
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



/** --------------------
 * Diseño lógico: evento: Dato --> iniciarSesion() -->
 * Dato <--
 * Descripción: Abre el panel adecuado al rol devuelto por el servidor
 * -------------------- */
async function iniciarSesion(evento) {
    await ejecutarFormulario(evento, async function realizarLogin() {
        // Diseño lógico: () -> realizarLogin -> promesa vacía. Descripción: Obtiene y aplica la sesión real.
        const datos = await logica.iniciarSesion({
            correo: elemento('correo').value, contrasena: elemento('contrasena').value,
        });
        tokenSesion = datos.token;
        usuarioActual = datos.usuario;
        elemento('contrasena').value = '';
        if (usuarioActual.debe_cambiar_contrasena) {
            mostrar('cambio-inicial');
            return;
        }
        await abrirPanel();
    });
}



/** --------------------
 * Diseño lógico: abrirPanel() -->
 * Dato <--
 * Descripción: Muestra las secciones que corresponden al rol (jefe, organización, técnico o trabajador)
 * -------------------- */
async function abrirPanel() {
    const rol = usuarioActual.rol;
    const jefe = rol === 'jefe_parque';
    const tecnico = rol === 'proveedor';
    const conSoporte = jefe || tecnico || rol === 'organizacion';
    mostrar('panel');
    elemento('ceja-panel').textContent = tecnico ? 'Panel técnico · OZORA' : rol === 'organizacion' ? 'Panel de la organización' : 'Panel del parque';
    elemento('saludo').textContent = `Hola, ${usuarioActual.nombre}.`;
    elemento('parque').textContent = usuarioActual.id_parque ? `Parque ${usuarioActual.id_parque}` : '';
    elemento('contenido-jefe').hidden = !jefe;
    elemento('contenido-trabajador').hidden = conSoporte;
    elemento('soporte').hidden = !conSoporte;
    for (const enlace of document.querySelectorAll('#navegacion-privada .solo-jefe')) enlace.hidden = !jefe;
    if (conSoporte) prepararSoporte(tecnico);
    if (jefe) await actualizarLectura();
    if (conSoporte) await cargarHilos();
}



/* ====================================================================================
 * Soporte técnico tipo foro. El técnico (rol proveedor) ve todas las consultas: las del
 * formulario de contacto de la web pública y las de jefes de parque y organizaciones.
 * Jefes y organizaciones abren consultas y siguen solo las suyas.
 * ==================================================================================== */
const ESTADOS_HILO = { abierto: 'Pendiente', respondido: 'Respondida', cerrado: 'Cerrada' };
let hiloAbierto = null;


/** Ajusta textos y controles del soporte al rol. */
function prepararSoporte(tecnico) {
    hiloAbierto = null;
    elemento('titulo-soporte').textContent = tecnico ? 'Bandeja de consultas' : 'Consultas y respuestas';
    elemento('explicacion-soporte').textContent = tecnico
        ? 'Aquí llegan las solicitudes del formulario de contacto y las consultas de jefes de parque y organizaciones. Al responder, la persona recibe también un correo.'
        : '¿Algo no funciona o tienes una duda? Abre una consulta: el equipo técnico de OZORA te responderá aquí y por correo.';
    elemento('filtro-soporte').hidden = !tecnico;
    elemento('form-consulta').hidden = tecnico;
    elemento('detalle-hilo').replaceChildren(parrafo('Selecciona una consulta para ver la conversación.', 'nota'));
}


/** Fecha corta y legible. */
function fechaCorta(iso) {
    return iso ? new Date(iso).toLocaleString('es-ES', { dateStyle: 'short', timeStyle: 'short' }) : '';
}


/** Etiqueta de estado con su color. */
function chipEstado(estado) {
    const chip = document.createElement('span');
    chip.className = `chip-estado estado-${estado}`;
    chip.textContent = ESTADOS_HILO[estado] || estado;
    return chip;
}


/** Carga la lista de consultas (con filtro de estado para el técnico). */
async function cargarHilos() {
    const lista = elemento('lista-hilos');
    const boton = elemento('recargar-soporte');
    boton.disabled = true;
    try {
        const estado = usuarioActual?.rol === 'proveedor' ? elemento('estado-soporte').value : '';
        const { hilos } = await conSesion(() => logica.listarHilos(tokenSesion, estado));
        lista.replaceChildren();
        if (!hilos.length) {
            const vacio = document.createElement('li');
            vacio.className = 'vacio';
            vacio.textContent = usuarioActual?.rol === 'proveedor' ? 'No hay consultas con este filtro.' : 'Aún no has abierto ninguna consulta.';
            lista.append(vacio);
        }
        for (const hilo of hilos) {
            const item = document.createElement('li');
            const boton = document.createElement('button');
            boton.type = 'button';
            boton.className = 'hilo' + (hilo.id_hilo === hiloAbierto ? ' activo' : '');
            boton.dataset.hilo = String(hilo.id_hilo);
            const cabecera = document.createElement('span');
            cabecera.className = 'hilo-cabecera';
            const asunto = document.createElement('strong');
            asunto.textContent = hilo.asunto;
            cabecera.append(asunto, chipEstado(hilo.estado));
            const meta = document.createElement('span');
            meta.className = 'hilo-meta';
            const origen = hilo.origen === 'contacto' ? 'Web pública' : 'Soporte';
            meta.textContent = (usuarioActual?.rol === 'proveedor' ? `${hilo.nombre_contacto} · ${hilo.organizacion_contacto} · ${origen} · ` : '')
                + `${hilo.mensajes ?? 0} mensaje${hilo.mensajes === 1 ? '' : 's'} · ${fechaCorta(hilo.actualizado_en)}`;
            boton.append(cabecera, meta);
            boton.addEventListener('click', () => abrirHilo(hilo.id_hilo));
            item.append(boton);
            lista.append(item);
        }
    } catch (error) {
        lista.replaceChildren(parrafo('No se han podido cargar las consultas. Pulsa Actualizar para reintentar.', 'nota'));
        mensaje(error.message, true);
    } finally { boton.disabled = false; }
}


/** Abre una consulta y muestra toda la conversación con el formulario de respuesta. */
async function abrirHilo(id) {
    const detalle = elemento('detalle-hilo');
    hiloAbierto = id;
    for (const boton of document.querySelectorAll('#lista-hilos .hilo')) boton.classList.toggle('activo', boton.dataset.hilo === String(id));
    detalle.replaceChildren(parrafo('Cargando conversación…', 'nota'));
    try {
        const { hilo, mensajes } = await conSesion(() => logica.verHilo(tokenSesion, id));
        const tecnico = usuarioActual?.rol === 'proveedor';
        const cabecera = document.createElement('div');
        cabecera.className = 'fila';
        const titulo = document.createElement('h3');
        titulo.textContent = hilo.asunto;
        cabecera.append(titulo, chipEstado(hilo.estado));
        const meta = parrafo(tecnico
            ? `${hilo.nombre_contacto} <${hilo.correo_contacto}> · ${hilo.organizacion_contacto} · ${hilo.origen === 'contacto' ? 'Formulario de la web pública' : 'Soporte del panel'}`
            : `Abierta el ${fechaCorta(hilo.creado_en)}`, 'nota');
        const conversacion = document.createElement('ol');
        conversacion.className = 'conversacion';
        for (const m of mensajes) {
            const burbuja = document.createElement('li');
            burbuja.className = 'burbuja' + (m.es_tecnico ? ' tecnico' : '');
            const autor = document.createElement('span');
            autor.className = 'autor';
            autor.textContent = `${m.autor} · ${fechaCorta(m.creado_en)}`;
            const texto = document.createElement('p');
            texto.textContent = m.texto; // textContent: el texto del usuario nunca se interpreta como HTML.
            burbuja.append(autor, texto);
            conversacion.append(burbuja);
        }
        detalle.replaceChildren(cabecera, meta, conversacion);
        if (hilo.estado !== 'cerrado' || tecnico) detalle.append(formularioRespuesta(hilo, tecnico));
        else detalle.append(parrafo('Esta consulta está cerrada. Si necesitas más ayuda, abre una nueva.', 'nota'));
        if (tecnico) {
            const alternar = document.createElement('button');
            alternar.type = 'button';
            alternar.className = 'secundario';
            alternar.textContent = hilo.estado === 'cerrado' ? 'Reabrir consulta' : 'Marcar como resuelta y cerrar';
            alternar.addEventListener('click', async () => {
                alternar.disabled = true;
                try {
                    const r = await conSesion(() => logica.cambiarEstadoHilo(tokenSesion, id, { estado: hilo.estado === 'cerrado' ? 'abierto' : 'cerrado' }));
                    await cargarHilos();
                    await abrirHilo(id);
                    mensaje(r.mensaje);
                } catch (error) { mensaje(error.message, true); alternar.disabled = false; }
            });
            detalle.append(alternar);
        }
        conversacion.lastElementChild?.scrollIntoView({ block: 'nearest' });
    } catch (error) {
        detalle.replaceChildren(parrafo(error.message, 'nota'));
    }
}


/** Formulario para responder a una consulta. */
function formularioRespuesta(hilo, tecnico) {
    const formulario = document.createElement('form');
    formulario.id = 'form-respuesta';
    formulario.className = 'form-respuesta';
    const etiqueta = document.createElement('label');
    etiqueta.htmlFor = 'texto-respuesta';
    etiqueta.textContent = tecnico ? `Responder a ${hilo.nombre_contacto}` : 'Añadir un mensaje';
    const campo = document.createElement('textarea');
    campo.id = 'texto-respuesta';
    campo.name = 'texto';
    campo.rows = 3;
    campo.minLength = 2;
    campo.maxLength = 4000;
    campo.required = true;
    campo.placeholder = tecnico ? 'Escribe la respuesta. Le llegará también por correo.' : 'Añade más detalles o responde al técnico…';
    const aviso = document.createElement('div');
    aviso.id = 'aviso-form-respuesta';
    aviso.className = 'aviso-formulario';
    aviso.setAttribute('role', 'status');
    aviso.tabIndex = -1;
    aviso.hidden = true;
    const enviar = document.createElement('button');
    enviar.type = 'submit';
    enviar.textContent = tecnico ? 'Enviar respuesta' : 'Enviar mensaje';
    formulario.append(etiqueta, campo, aviso, enviar);
    formulario.addEventListener('submit', evento => ejecutarFormulario(evento, async () => {
        const r = await conSesion(() => logica.responderHilo(tokenSesion, hilo.id_hilo, { texto: campo.value }));
        await cargarHilos();
        await abrirHilo(hilo.id_hilo);
        mensaje(r.mensaje);
    }));
    return formulario;
}


/** Jefe u organización abre una consulta nueva. */
async function crearConsulta(evento) {
    await ejecutarFormulario(evento, async function enviarConsulta() {
        const datos = await conSesion(() => logica.crearHilo(tokenSesion, {
            asunto: elemento('asunto-consulta').value, mensaje: elemento('mensaje-consulta').value,
        }));
        elemento('form-consulta').reset();
        mensaje(datos.mensaje, false, 'aviso-form-consulta');
        await cargarHilos();
        await abrirHilo(datos.id_hilo);
    });
}



/** --------------------
 * Diseño lógico: evento: Dato --> registrarTrabajador() -->
 * Dato <--
 * Descripción: Crea la cuenta del parque con contraseña inicial elegida por el jefe
 * -------------------- */
async function registrarTrabajador(evento) {
    await ejecutarFormulario(evento, async function realizarAlta() {
        // Diseño lógico: () -> realizarAlta -> promesa vacía. Descripción: Envía los datos del alta al servidor.
        const contrasena = elemento('contrasena-trabajador').value;
        const problema = problemaContrasena(contrasena, elemento('correo-trabajador').value);
        if (problema) throw new Error(problema);
        const datos = await conSesion(() => logica.registrarTrabajador(tokenSesion, { nombre: elemento('nombre-trabajador').value,
            correo: elemento('correo-trabajador').value, contrasena_inicial: contrasena }));
        elemento('form-alta').reset();
        ocultarContrasenas(elemento('form-alta'));
        mensaje(datos.mensaje || 'Cuenta creada. El trabajador debe cambiar la contraseña inicial en su primer acceso.', false, 'aviso-form-alta');
    });
}



/** --------------------
 * Diseño lógico: evento: Dato --> solicitarRecuperacion() -->
 * Dato <--
 * Descripción: Solicita un enlace sin revelar cuentas registradas
 * -------------------- */
async function solicitarRecuperacion(evento) {
    await ejecutarFormulario(evento, async function realizarSolicitud() {
        // Diseño lógico: () -> realizarSolicitud -> promesa vacía. Descripción: Presenta la respuesta genérica de recuperación.
        const datos = await logica.solicitarRecuperacion({ correo: elemento('correo-recuperacion').value });
        mensaje(datos.mensaje, false, 'aviso-form-recuperacion');
    });
}



/** --------------------
 * Diseño lógico: evento: Dato --> restablecerContrasena() -->
 * Dato <--
 * Descripción: Comprueba la repetición y consume el enlace de alta o recuperación
 * -------------------- */
async function restablecerContrasena(evento) {
    await ejecutarFormulario(evento, async function realizarCambio() {
        // Diseño lógico: () -> realizarCambio -> promesa vacía. Descripción: Cambia la contraseña y vuelve al acceso.
        const nueva = elemento('nueva').value;
        if (nueva !== elemento('repetir').value) throw new Error('Las contraseñas no coinciden.');
        const problema = problemaContrasena(nueva, usuarioActual?.email || '');
        if (problema) throw new Error(problema);
        await logica.restablecerContrasena({ token: tokenRecuperacion, contrasena_nueva: nueva });
        tokenRecuperacion = '';
        tokenSesion = '';
        usuarioActual = null;
        elemento('form-restablecer').reset();
        mostrar('acceso');
        mensaje('Contraseña actualizada. Ya puedes iniciar sesión con ella.');
    });
}



/** --------------------
 * Diseño lógico: evento: Dato --> cambiarContrasenaInicial() -->
 * Dato <--
 * Descripción: Permite probar el mismo cambio autenticado que utilizará Android
 * -------------------- */
async function cambiarContrasenaInicial(evento) {
    await ejecutarFormulario(evento, async function realizarCambioInicial() {
        // Diseño lógico: () -> realizarCambioInicial -> promesa vacía. Descripción: Cambia la clave propia y solicita un login nuevo.
        const nueva = elemento('nueva-inicial').value;
        if (nueva !== elemento('repetir-inicial').value) throw new Error('Las contraseñas no coinciden.');
        const problema = problemaContrasena(nueva, usuarioActual?.email || '');
        if (problema) throw new Error(problema);
        await conSesion(() => logica.cambiarContrasena(tokenSesion, {
            contrasena_actual: elemento('actual-inicial').value, contrasena_nueva: nueva,
        }));
        tokenSesion = '';
        usuarioActual = null;
        elemento('form-cambio-inicial').reset();
        mostrar('acceso');
        mensaje('Contraseña cambiada. Inicia sesión con la nueva contraseña.');
    });
}



/** --------------------
 * Diseño lógico: cerrarSesion() -->
 * Dato <--
 * Descripción: Revoca la sesión remota y conserva la posibilidad de reintentar si falla
 * -------------------- */
async function cerrarSesion() {
    elemento('salir').disabled = true;
    try {
        await conSesion(() => logica.cerrarSesion(tokenSesion));
        tokenSesion = '';
        usuarioActual = null;
        elemento('lectura').replaceChildren();
        elemento('form-alta').reset();
        elemento('form-cambio-inicial').reset();
        elemento('form-consulta').reset();
        elemento('lista-hilos').replaceChildren();
        hiloAbierto = null;
        mostrar('acceso');
        mensaje('Sesión cerrada.');
    } catch (error) { mensaje(error.message, true); }
    finally { elemento('salir').disabled = false; }
}



/** --------------------
 * Diseño lógico: abrirRecuperacion() -->
 * Dato <--
 * Descripción: Muestra el formulario de solicitud de enlace
 * -------------------- */
function abrirRecuperacion() { mostrar('recuperacion'); }


/** --------------------
 * Diseño lógico: volverAcceso() -->
 * Dato <--
 * Descripción: Descarta el enlace local y limpia los campos de contraseña
 * -------------------- */
function volverAcceso() { tokenRecuperacion = ''; elemento('form-restablecer').reset(); mostrar('acceso'); }



/** --------------------
 * Diseño lógico: evento: Dato --> alternarContrasena() -->
 * Dato <--
 * Descripción: Muestra u oculta exclusivamente el campo asociado al botón
 * -------------------- */
function alternarContrasena(evento) {
    const boton = evento.currentTarget;
    const campo = elemento(boton.getAttribute('aria-controls'));
    const visible = campo.type === 'password';
    campo.type = visible ? 'text' : 'password';
    const etiqueta = visible ? 'Ocultar contraseña' : 'Mostrar contraseña';
    boton.setAttribute('aria-label', etiqueta);
    boton.title = etiqueta;
    boton.setAttribute('aria-pressed', String(visible));
}



/** --------------------
 * Diseño lógico: prepararVisibilidadContrasenas() -->
 * Dato <--
 * Descripción: Añade controles accesibles a las claves de acceso, alta y cambio
 * -------------------- */
function prepararVisibilidadContrasenas() {
    for (const campo of document.querySelectorAll('input[type="password"]')) {
        const boton = document.createElement('button');
        boton.type = 'button'; // Mostrar la clave nunca debe enviar el formulario.
        boton.className = 'ver-contrasena';
        boton.setAttribute('aria-label', 'Mostrar contraseña');
        boton.title = 'Mostrar contraseña';
        // Icono propio sin dependencias externas; el nombre accesible queda en el botón.
        boton.innerHTML = '<svg viewBox="0 0 24 24" aria-hidden="true" focusable="false"><path d="M2 12s3.5-7 10-7 10 7 10 7-3.5 7-10 7S2 12 2 12Z"/><circle cx="12" cy="12" r="3"/><path class="ojo-tachado" d="m3 3 18 18"/></svg>';
        boton.dataset.verContrasena = '';
        boton.setAttribute('aria-controls', campo.id);
        boton.setAttribute('aria-pressed', 'false');
        boton.addEventListener('click', alternarContrasena);
        const grupo = document.createElement('div');
        grupo.className = 'campo-contrasena';
        campo.before(grupo);
        grupo.append(campo, boton);
    }
}

prepararVisibilidadContrasenas();



/** --------------------
 * Diseño lógico: prepararAvisosFormularios() -->
 * Dato <--
 * Descripción: Coloca cada resultado junto al botón donde se realiza la operación
 * -------------------- */
function prepararAvisosFormularios() {
    for (const formulario of document.querySelectorAll('form')) {
        const aviso = document.createElement('div');
        aviso.id = `aviso-${formulario.id}`;
        aviso.className = 'aviso-formulario';
        aviso.setAttribute('role', 'status');
        aviso.setAttribute('aria-live', 'polite');
        aviso.tabIndex = -1;
        aviso.hidden = true;
        formulario.querySelector('button[type="submit"]').before(aviso);
    }
}
prepararAvisosFormularios();

elemento('form-login').addEventListener('submit', iniciarSesion);
elemento('form-alta').addEventListener('submit', registrarTrabajador);
elemento('form-recuperacion').addEventListener('submit', solicitarRecuperacion);
elemento('form-restablecer').addEventListener('submit', restablecerContrasena);
elemento('form-cambio-inicial').addEventListener('submit', cambiarContrasenaInicial);
elemento('salir-cambio').addEventListener('click', cerrarSesion);
elemento('abrir-recuperacion').addEventListener('click', abrirRecuperacion);
elemento('volver-acceso').addEventListener('click', volverAcceso);
elemento('cancelar-restablecer').addEventListener('click', volverAcceso);
elemento('actualizar').addEventListener('click', actualizarLectura);
elemento('salir').addEventListener('click', cerrarSesion);
elemento('form-consulta').addEventListener('submit', crearConsulta);
elemento('recargar-soporte').addEventListener('click', cargarHilos);
elemento('estado-soporte').addEventListener('change', cargarHilos);

const fragmento = new URLSearchParams(location.hash.slice(1));
if (fragmento.has('token')) {
    tokenRecuperacion = fragmento.get('token');
    history.replaceState(null, '', location.pathname);
    mostrar('restablecer');
}
