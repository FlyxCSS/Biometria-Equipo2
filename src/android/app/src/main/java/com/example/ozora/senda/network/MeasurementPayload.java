/*
 * Nombre: MeasurementPayload.java
 * Descripción: Adaptador del modelo BLE al contrato REST de mediciones.
 * Copyright: (c) 2026 Equipo 2 - SENDA / OZORA.
 * Fecha: 03/11/2026.
 * Autor: Julia (con asistencia de Codex).
 * Aportación: Compartir el contrato con PHP sin enviar campos locales adicionales.
 */
package com.example.ozora.senda.network;

import com.example.ozora.senda.model.MedicionAmbiental;
import java.util.LinkedHashMap;
import java.util.Map;

public final class MeasurementPayload {
    private MeasurementPayload() {}

    /** Diseño lógico: (medición BLE geolocalizada) -> datos -> campos JSON.
     * Descripción: Conserva ppb, fecha UTC y coordenadas; el servidor comprueba la asignación. */
    public static Map<String,Object> datos(MedicionAmbiental m) {
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
