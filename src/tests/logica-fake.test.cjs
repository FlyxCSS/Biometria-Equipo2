const { test } = require('node:test');
const assert = require('node:assert/strict');
const { readFileSync } = require('node:fs');
const { runInNewContext } = require('node:vm');
const { resolve } = require('node:path');

function cliente(responder) {
    const peticiones = [];
    const LogicaFake = runInNewContext(readFileSync(resolve(__dirname,
        '../frontend_business_logic/LogicaFake.js'), 'utf8') + '\nLogicaFake;', {
        AbortSignal,
        fetch: async (url, opciones) => {
            peticiones.push({ url, ...opciones });
            return responder();
        },
    });
    return { logica: new LogicaFake('/senda'), peticiones };
}

test('operaciones con nombres de la lógica real envían método, token y JSON', async () => {
    const { logica, peticiones } = cliente(() => ({
        ok: true, status: 200, json: async () => ({ ok: true, data: { aceptada: true } }),
    }));
    const datos = { correo: 'test@example.org' };
    const casos = [
        ['iniciarSesion', [datos], 'POST', '/sesiones', false, datos],
        ['consultarSesion', ['test'], 'GET', '/sesiones/actual', true, null],
        ['cerrarSesion', ['test'], 'DELETE', '/sesiones/actual', true, null],
        ['ultimaMedicion', ['test', 3], 'GET', '/parques/3/mediciones/ultima', true, null],
        ['registrarTrabajador', ['test', datos], 'POST', '/trabajadores', true, datos],
        ['cambiarContrasena', ['test', datos], 'POST', '/usuarios/actual/contrasena', true, datos],
        ['solicitarRecuperacion', [datos], 'POST', '/recuperaciones', false, datos],
        ['restablecerContrasena', [datos], 'POST', '/recuperaciones/confirmacion', false, datos],
        ['registrarSolicitud', [datos], 'POST', '/solicitudes', false, datos],
    ];
    for (const [nombre, argumentos, metodo, ruta, autenticada, cuerpo] of casos) {
        assert.equal((await logica[nombre](...argumentos)).aceptada, true);
        const p = peticiones.at(-1);
        assert.equal(p.url, '/senda/api/v1' + ruta);
        assert.equal(p.method, metodo);
        assert.equal(p.headers.Authorization, autenticada ? 'Bearer test' : undefined);
        assert.equal(p.body, cuerpo === null ? null : JSON.stringify(cuerpo));
        assert.equal(p.credentials, 'omit');
    }
});

test('GET de última medición conserva el estado vacío', async () => {
    const { logica } = cliente(() => ({
        ok: true, status: 200, json: async () => ({ ok: true, data: { medicion: null } }),
    }));
    assert.equal((await logica.ultimaMedicion('test', 3)).medicion, null);
});

test('errores conservan el estado HTTP y los campos para la interfaz', async () => {
    const { logica } = cliente(() => ({
        ok: false, status: 401, json: async () => ({
            ok: false, error: { mensaje: 'Sesión caducada', campos: { correo: 'Inválido' } },
        }),
    }));
    await assert.rejects(logica.consultarSesion('test'), e =>
        e.status === 401 && e.campos.correo === 'Inválido' && e.message === 'Sesión caducada');
});

test('JSON inválido y fallo de red se propagan como error', async () => {
    const invalido = cliente(() => ({ json: async () => { throw new Error('HTML'); } }));
    await assert.rejects(invalido.logica.iniciarSesion({}), /respuesta válida/);
    const sinRed = cliente(() => { throw new Error('Sin red'); });
    await assert.rejects(sinRed.logica.iniciarSesion({}), /contactar con el servidor/);
});

test('rechaza identificadores de parque que alteren la ruta', async () => {
    const { logica, peticiones } = cliente(() => { throw new Error('No debe enviar'); });
    await assert.rejects(logica.ultimaMedicion('test', '../sesiones'), /parque inválido/);
    assert.equal(peticiones.length, 0);
});
