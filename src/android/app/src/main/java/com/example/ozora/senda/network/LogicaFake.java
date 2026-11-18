package com.example.ozora.senda.network;

import android.content.Context;
import com.example.ozora.senda.model.MedicionAmbiental;
import org.json.JSONObject;

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

    public JSONObject guardarMedicion(String token, MedicionAmbiental medicion) throws Exception {
        return transporte.pedir("POST", "/mediciones", token,
                new JSONObject(MeasurementPayload.datos(medicion)));
    }

    public JSONObject cambiarContrasena(String token, JSONObject datos) throws Exception {
        return transporte.pedir("POST", "/usuarios/actual/contrasena", token, datos);
    }

    public JSONObject solicitarRecuperacion(JSONObject datos) throws Exception {
        return transporte.pedir("POST", "/recuperaciones", null, datos);
    }

    public JSONObject restablecerContrasena(JSONObject datos) throws Exception {
        return transporte.pedir("POST", "/recuperaciones/confirmacion", null, datos);
    }
}
