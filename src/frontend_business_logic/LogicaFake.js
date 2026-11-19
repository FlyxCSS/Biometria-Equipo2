/*
 * Nombre: LogicaFake.js
 * Descripción: Expone operaciones del negocio y encapsula su invocación remota.
 * Copyright: (c) 2026 Equipo 2 - SENDA / OZORA.
 * Fecha: 19/11/2026
 * Autores: Fabián Andrés Useche Rivera; Élia Garcia Pons; Judith Civico Aporta; Julia Valen de Oliveira; Soufiane Belfquih El Khalfi
 * Aportación: Expone operaciones del negocio y encapsula su invocación remota.
 */
'use strict';

/** Proxy web de las operaciones públicas de LogicaNegocio.php. */
class LogicaFake {
    #base;

    /** --------------------
     * Diseño lógico: rutaBase: Dato --> constructor() -->
     * Dato <--
     * Descripción: Construye LogicaFake. Expone operaciones del negocio y encapsula su invocación remota
     * -------------------- */
    constructor(rutaBase = '') {
        this.#base = rutaBase;
    }


    /** --------------------
     * Diseño lógico: datos: Dato --> iniciarSesion() -->
     * Dato <--
     * Descripción: Iniciar Sesion. Expone operaciones del negocio y encapsula su invocación remota
     * -------------------- */
    iniciarSesion(datos) {
        return this.#pedir('/sesiones', 'POST', '', datos);
    }


    /** --------------------
     * Diseño lógico: token: Text --> consultarSesion() -->
     * Dato <--
     * Descripción: Consultar Sesion. Expone operaciones del negocio y encapsula su invocación remota
     * -------------------- */
    consultarSesion(token) {
        return this.#pedir('/sesiones/actual', 'GET', token);
    }


    /** --------------------
     * Diseño lógico: token: Text --> cerrarSesion() -->
     * Dato <--
     * Descripción: Cerrar Sesion. Expone operaciones del negocio y encapsula su invocación remota
     * -------------------- */
    cerrarSesion(token) {
        return this.#pedir('/sesiones/actual', 'DELETE', token);
    }


    /** --------------------
     * Diseño lógico: token: Text, parque: Dato --> ultimaMedicion() -->
     * Dato <--
     * Descripción: Ultima Medicion. Expone operaciones del negocio y encapsula su invocación remota
     * -------------------- */
    ultimaMedicion(token, parque) {
        if (!Number.isSafeInteger(parque) || parque <= 0 || parque > 999999999) {
            return Promise.reject(new Error('Identificador de parque inválido.'));
        }
        return this.#pedir('/parques/' + parque + '/mediciones/ultima', 'GET', token);
    }


    /** --------------------
     * Diseño lógico: token: Text, datos: Dato --> registrarTrabajador() -->
     * Dato <--
     * Descripción: Registrar Trabajador. Expone operaciones del negocio y encapsula su invocación remota
     * -------------------- */
    registrarTrabajador(token, datos) {
        return this.#pedir('/trabajadores', 'POST', token, datos);
    }


    /** --------------------
     * Diseño lógico: token: Text, datos: Dato --> cambiarContrasena() -->
     * Dato <--
     * Descripción: Cambiar Contrasena. Expone operaciones del negocio y encapsula su invocación remota
     * -------------------- */
    cambiarContrasena(token, datos) {
        return this.#pedir('/usuarios/actual/contrasena', 'POST', token, datos);
    }


    /** --------------------
     * Diseño lógico: datos: Dato --> solicitarRecuperacion() -->
     * Dato <--
     * Descripción: Solicitar Recuperacion. Expone operaciones del negocio y encapsula su invocación remota
     * -------------------- */
    solicitarRecuperacion(datos) {
        return this.#pedir('/recuperaciones', 'POST', '', datos);
    }


    /** --------------------
     * Diseño lógico: datos: Dato --> restablecerContrasena() -->
     * Dato <--
     * Descripción: Restablecer Contrasena. Expone operaciones del negocio y encapsula su invocación remota
     * -------------------- */
    restablecerContrasena(datos) {
        return this.#pedir('/recuperaciones/confirmacion', 'POST', '', datos);
    }


    /** --------------------
     * Diseño lógico: datos: Dato --> registrarSolicitud() -->
     * Dato <--
     * Descripción: Registrar Solicitud. Expone operaciones del negocio y encapsula su invocación remota
     * -------------------- */
    registrarSolicitud(datos) {
        return this.#pedir('/solicitudes', 'POST', '', datos);
    }


    /** --------------------
     * Diseño lógico: ruta: Text, metodo: Text, token: Text, datos: Dato --> #pedir() -->
     * Dato <--
     * Descripción: #pedir. Expone operaciones del negocio y encapsula su invocación remota
     * -------------------- */
    async #pedir(ruta, metodo, token, datos = null) {
        const headers = { Accept: 'application/json' };
        if (token) headers.Authorization = 'Bearer ' + token;
        if (datos !== null) headers['Content-Type'] = 'application/json';
        let respuesta;
        try {
            respuesta = await fetch(this.#base + '/api/v1' + ruta, {
                method: metodo, headers, body: datos === null ? null : JSON.stringify(datos),
                cache: 'no-store', credentials: 'omit', signal: AbortSignal.timeout(15000),
            });
        } catch (causa) {
            throw new Error('No se ha podido contactar con el servidor. Comprueba la conexión antes de reintentar.');
        }
        let contenido;
        try { contenido = await respuesta.json(); }
        catch (causa) { throw new Error('El servidor no ha devuelto una respuesta válida.'); }
        if (!respuesta.ok || contenido.ok !== true) {
            const error = new Error(contenido.error?.mensaje || 'No se ha podido completar la operación.');
            error.status = respuesta.status;
            error.campos = contenido.error?.campos || {};
            throw error;
        }
        return contenido.data;
    }
}
