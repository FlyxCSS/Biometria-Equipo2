/*
 * Nombre: LogicaFake.java
 * Descripción: Expone operaciones del negocio y encapsula su invocación remota.
 * Copyright: (c) 2026 Equipo 2 - SENDA / OZORA.
 * Fecha: 19/11/2026
 * Autores: Fabián Andrés Useche Rivera; Élia Garcia Pons; Judith Civico Aporta; Julia Valen de Oliveira; Soufiane Belfquih El Khalfi
 * Aportación: Expone operaciones del negocio y encapsula su invocación remota.
 */
package com.example.ozora.senda.network;

import android.content.Context;
import com.example.ozora.senda.model.MedicionAmbiental;
import com.example.ozora.senda.model.MedicionTemperatura;
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


    /** --------------------
     * Diseño lógico: contexto: Entorno --> LogicaFake() -->
     * LogicaFake <--
     * Descripción: Construye LogicaFake. Expone operaciones del negocio y encapsula su invocación remota
     * -------------------- */
    public LogicaFake(Context contexto) {
        this(new ApiClient(contexto));
    }


    /** --------------------
     * Diseño lógico: transporte: ApiClient --> LogicaFake() -->
     * LogicaFake <--
     * Descripción: Construye LogicaFake. Expone operaciones del negocio y encapsula su invocación remota
     * -------------------- */
    LogicaFake(ApiClient transporte) {
        this.transporte = transporte;
    }


    /** --------------------
     * Diseño lógico: datos: Datos --> iniciarSesion() -->
     * Datos <--
     * Descripción: Iniciar Sesion. Expone operaciones del negocio y encapsula su invocación remota
     * -------------------- */
    public JSONObject iniciarSesion(JSONObject datos) throws Exception {
        return transporte.pedir("POST", "/sesiones", null, datos);
    }


    /** --------------------
     * Diseño lógico: token: Text --> consultarSesion() -->
     * Datos <--
     * Descripción: Consultar Sesion. Expone operaciones del negocio y encapsula su invocación remota
     * -------------------- */
    public JSONObject consultarSesion(String token) throws Exception {
        return transporte.pedir("GET", "/sesiones/actual", token, null);
    }


    /** --------------------
     * Diseño lógico: token: Text --> cerrarSesion() -->
     * Datos <--
     * Descripción: Cerrar Sesion. Expone operaciones del negocio y encapsula su invocación remota
     * -------------------- */
    public JSONObject cerrarSesion(String token) throws Exception {
        return transporte.pedir("DELETE", "/sesiones/actual", token, null);
    }


    /** --------------------
     * Diseño lógico: token: Text, foto: Text --> actualizarFotoPerfil() -->
     * Datos <--
     * Descripción: Actualizar Foto Perfil. Expone operaciones del negocio y encapsula su invocación remota
     * -------------------- */
    public JSONObject actualizarFotoPerfil(String token, String foto) throws Exception {
        return transporte.pedir("POST", "/usuarios/actual/foto", token,
                new JSONObject().put("foto_perfil", foto == null ? JSONObject.NULL : foto));
    }


    /** --------------------
     * Diseño lógico: token: Text, identificador: Text --> vincularLlavero() -->
     * Datos <--
     * Descripción: Vincular Llavero. Expone operaciones del negocio y encapsula su invocación remota
     * -------------------- */
    public JSONObject vincularLlavero(String token, String identificador) throws Exception {
        return transporte.pedir("POST", "/llaveros/actual", token,
                new JSONObject().put("nombre_micro", identificador));
    }


    /** --------------------
     * Diseño lógico: token: Text --> desvincularLlavero() -->
     * Datos <--
     * Descripción: Desvincular Llavero. Expone operaciones del negocio y encapsula su invocación remota
     * -------------------- */
    public JSONObject desvincularLlavero(String token) throws Exception {
        return transporte.pedir("DELETE", "/llaveros/actual", token, null);
    }


    /** --------------------
     * Diseño lógico: token: Text, medicion: MedicionAmbiental --> guardarMedicion() -->
     * Datos <--
     * Descripción: Guardar Medicion. Expone operaciones del negocio y encapsula su invocación remota
     * -------------------- */
    public JSONObject guardarMedicion(String token, MedicionAmbiental medicion) throws Exception {
        return guardarDatos(token, datosMedicion(medicion));
    }


    /** --------------------
     * Diseño lógico: token: Text, medicion: MedicionTemperatura --> guardarTemperatura() -->
     * Datos <--
     * Descripción: Guardar Temperatura. Expone operaciones del negocio y encapsula su invocación remota
     * -------------------- */
    public JSONObject guardarTemperatura(String token, MedicionTemperatura medicion) throws Exception {
        return guardarDatos(token, datosTemperatura(medicion));
    }


    /** --------------------
     * Diseño lógico: token: Text, datos: [Dato] --> guardarDatos() -->
     * Datos <--
     * Descripción: Guardar Datos. Expone operaciones del negocio y encapsula su invocación remota
     * -------------------- */
    private JSONObject guardarDatos(String token, Map<String,Object> datos) throws Exception {
        JSONObject respuesta = transporte.pedir("POST", "/mediciones", token, new JSONObject(datos));
        if (!Boolean.TRUE.equals(respuesta.opt("guardada"))) {
            throw new IOException("El servidor no ha confirmado el guardado de la medición.");
        }
        return respuesta;
    }


    /** --------------------
     * Diseño lógico: m: MedicionTemperatura --> datosTemperatura() --x
     * [Dato] <--
     * Descripción: Datos Temperatura. Expone operaciones del negocio y encapsula su invocación remota
     * -------------------- */
    static Map<String,Object> datosTemperatura(MedicionTemperatura m) {
        if (m == null || m.idLlavero == null || m.fechaIso8601 == null
                || m.centesimas < -4000 || m.centesimas > 5000
                || !Double.isFinite(m.latitud) || !Double.isFinite(m.longitud)
                || Math.abs(m.latitud) > 90 || Math.abs(m.longitud) > 180)
            throw new IllegalArgumentException("Temperatura inválida para enviar.");
        Map<String,Object> datos = new LinkedHashMap<>();
        datos.put("nombre_micro", m.idLlavero);
        datos.put("tipo", "TEMPERATURA");
        datos.put("valor", m.centesimas / 100.0);
        datos.put("unidad", "C");
        datos.put("latitud", m.latitud);
        datos.put("longitud", m.longitud);
        datos.put("fecha_hora", m.fechaIso8601);
        datos.put("simulada", false);
        return datos;
    }


    /** --------------------
     * Diseño lógico: token: Text, datos: Datos --> cambiarContrasena() -->
     * Datos <--
     * Descripción: Cambiar Contrasena. Expone operaciones del negocio y encapsula su invocación remota
     * -------------------- */
    public JSONObject cambiarContrasena(String token, JSONObject datos) throws Exception {
        return transporte.pedir("POST", "/usuarios/actual/contrasena", token, datos);
    }


    /** --------------------
     * Diseño lógico: token: Text, cursor: Text --> historialMediciones() -->
     * Datos <--
     * Descripción: Historial Mediciones. Expone operaciones del negocio y encapsula su invocación remota
     * -------------------- */
    public JSONObject historialMediciones(String token, String cursor) throws Exception {
        return transporte.pedir("GET", "/usuarios/actual/mediciones" + (cursor == null ? ""
                : "?cursor=" + java.net.URLEncoder.encode(cursor, "UTF-8")), token, null);
    }


    /** --------------------
     * Diseño lógico: datos: Datos --> solicitarRecuperacion() -->
     * Datos <--
     * Descripción: Solicitar Recuperacion. Expone operaciones del negocio y encapsula su invocación remota
     * -------------------- */
    public JSONObject solicitarRecuperacion(JSONObject datos) throws Exception {
        return transporte.pedir("POST", "/recuperaciones", null, datos);
    }


    /** --------------------
     * Diseño lógico: datos: Datos --> restablecerContrasena() -->
     * Datos <--
     * Descripción: Restablecer Contrasena. Expone operaciones del negocio y encapsula su invocación remota
     * -------------------- */
    public JSONObject restablecerContrasena(JSONObject datos) throws Exception {
        return transporte.pedir("POST", "/recuperaciones/confirmacion", null, datos);
    }



    /** --------------------
     * Diseño lógico: m: MedicionAmbiental --> datosMedicion() --x
     * [Dato] <--
     * Descripción: Conserva ppb, fecha UTC y coordenadas; el servidor comprueba la asignación
     * -------------------- */
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
