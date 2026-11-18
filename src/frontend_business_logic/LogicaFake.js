'use strict';

/** Proxy web de las operaciones públicas de LogicaNegocio.php. */
class LogicaFake {
    constructor(rutaBase = '') {
        this.base = rutaBase;
    }

    iniciarSesion(datos) {
        return this.#pedir('/sesiones', 'POST', '', datos);
    }

    consultarSesion(token) {
        return this.#pedir('/sesiones/actual', 'GET', token);
    }

    cerrarSesion(token) {
        return this.#pedir('/sesiones/actual', 'DELETE', token);
    }

    ultimaMedicion(token, parque) {
        if (!Number.isSafeInteger(parque) || parque <= 0 || parque > 999999999) {
            return Promise.reject(new Error('Identificador de parque inválido.'));
        }
        return this.#pedir('/parques/' + parque + '/mediciones/ultima', 'GET', token);
    }

    registrarTrabajador(token, datos) {
        return this.#pedir('/trabajadores', 'POST', token, datos);
    }

    cambiarContrasena(token, datos) {
        return this.#pedir('/usuarios/actual/contrasena', 'POST', token, datos);
    }

    solicitarRecuperacion(datos) {
        return this.#pedir('/recuperaciones', 'POST', '', datos);
    }

    restablecerContrasena(datos) {
        return this.#pedir('/recuperaciones/confirmacion', 'POST', '', datos);
    }

    registrarSolicitud(datos) {
        return this.#pedir('/solicitudes', 'POST', '', datos);
    }

    async #pedir(ruta, metodo, token, datos = null) {
        const headers = { Accept: 'application/json' };
        if (token) headers.Authorization = 'Bearer ' + token;
        if (datos !== null) headers['Content-Type'] = 'application/json';
        let respuesta;
        try {
            respuesta = await fetch(this.base + '/api/v1' + ruta, {
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
