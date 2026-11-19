package com.example.ozora.senda.network;

import android.content.Context;
import com.example.ozora.senda.model.MedicionAmbiental;
import org.json.JSONObject;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Proxy Android de LogicaNegocio.php. Cada operación construye la petición de
 * ServidorRest.php y devuelve sus datos o propaga el error del transporte.
 * Ejecutar fuera del hilo de interfaz, como hacen login y la cola de envío.
 */
public final class LogicaFake {
    private final ApiClient transporte;

    public LogicaFake(Context contexto) {
        this(new ApiClient(contexto));
    }

    LogicaFake(ApiClient transporte) {
        this.transporte = transporte;
    }

    public JSONObject iniciarSesion(JSONObject datos) throws Exception {
        return transporte.pedir("POST", "/sesiones", null, datos);
    }

    public JSONObject consultarSesion(String token) throws Exception {
        return transporte.pedir("GET", "/sesiones/actual", token, null);
    }

    public JSONObject cerrarSesion(String token) throws Exception {
        return transporte.pedir("DELETE", "/sesiones/actual", token, null);
    }

    public JSONObject actualizarFotoPerfil(String token, String foto) throws Exception {
        return transporte.pedir("POST", "/usuarios/actual/foto", token,
                new JSONObject().put("foto_perfil", foto == null ? JSONObject.NULL : foto));
    }

    public JSONObject vincularLlavero(String token, String identificador) throws Exception {
        return transporte.pedir("POST", "/llaveros/actual", token,
                new JSONObject().put("nombre_micro", identificador));
    }

    public JSONObject desvincularLlavero(String token) throws Exception {
        return transporte.pedir("DELETE", "/llaveros/actual", token, null);
    }

    public JSONObject guardarMedicion(String token, MedicionAmbiental medicion) throws Exception {
        JSONObject respuesta = transporte.pedir("POST", "/mediciones", token,
                new JSONObject(datosMedicion(medicion)));
        if (!Boolean.TRUE.equals(respuesta.opt("guardada"))) {
            throw new IOException("El servidor no ha confirmado el guardado de la medición.");
        }
        return respuesta;
    }

    public JSONObject cambiarContrasena(String token, JSONObject datos) throws Exception {
        return transporte.pedir("POST", "/usuarios/actual/contrasena", token, datos);
    }

    public JSONObject historialMediciones(String token, String cursor) throws Exception {
        return transporte.pedir("GET", "/usuarios/actual/mediciones" + (cursor == null ? ""
                : "?cursor=" + java.net.URLEncoder.encode(cursor, "UTF-8")), token, null);
    }

    public JSONObject solicitarRecuperacion(JSONObject datos) throws Exception {
        return transporte.pedir("POST", "/recuperaciones", null, datos);
    }

    public JSONObject restablecerContrasena(JSONObject datos) throws Exception {
        return transporte.pedir("POST", "/recuperaciones/confirmacion", null, datos);
    }

    /** Diseño lógico: (medición BLE geolocalizada) -> datos -> campos JSON.
     * Descripción: Conserva ppb, fecha UTC y coordenadas; el servidor comprueba la asignación. */
    static Map<String,Object> datosMedicion(MedicionAmbiental m) {
        if (m == null || m.idLlavero == null || m.fechaIso8601 == null
                || m.valorPpb < 0 || m.valorPpb > 65535
                || !Double.isFinite(m.latitud) || !Double.isFinite(m.longitud)
                || Math.abs(m.latitud) > 90 || Math.abs(m.longitud) > 180)
            throw new IllegalArgumentException("Medición inválida para enviar.");
        Map<String,Object> datos = new LinkedHashMap<>();
        datos.put("nombre_micro", m.idLlavero);
        datos.put("tipo", "O3");
        datos.put("valor", m.valorPpb);
        datos.put("unidad", "ppb");
        datos.put("latitud", m.latitud);
        datos.put("longitud", m.longitud);
        datos.put("fecha_hora", m.fechaIso8601);
        datos.put("simulada", false);
        return datos;
    }
}
