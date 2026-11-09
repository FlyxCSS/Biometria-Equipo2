/*
 * Nombre: PreparadorMediciones.java
 * Descripción: Valida lecturas BLE y posiciones antes de construir mediciones.
 * Copyright (c) 2026 - Equipo 2 - SENDA / OZORA
 * Fecha: 02/11/2026
 * Autor: ...
 * Aportación: Preparación local de datos para integración REST, sin transmisión.
 */
package com.example.ozora.senda.integration;

import android.location.Location;
import android.os.SystemClock;
import com.example.ozora.senda.bluetooth.IBeaconParser;
import com.example.ozora.senda.model.MedicionAmbiental;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/** Filtrado de calidad local. No autentica llaveros ni envía información. */
public final class PreparadorMediciones {
    public static final long MAX_EDAD_UBICACION_MS = 10_000L;
    public static final float MAX_PRECISION_METROS = 100f;
    private final ControlContadores contadores = new ControlContadores();

    /** Diseño lógico: (String, IBeaconParser.Resultado, Location) -> preparar() -> (Resultado)
     * Descripción: Rechaza otra emisora, tramas no O3, ubicación antigua o imprecisa y duplicados recientes. */
    public Resultado preparar(String seleccionado, IBeaconParser.Resultado trama, Location ubicacion) {
        if (seleccionado == null || trama == null || trama.nombreBle == null
                || !seleccionado.equalsIgnoreCase(trama.nombreBle)) {
            return Resultado.error("La trama no corresponde al llavero seleccionado.");
        }
        if (!trama.esOzono()) return Resultado.error("La trama no contiene O3.");
        if (ubicacion == null) return Resultado.error("Esperando la primera ubicación.");
        long edadMs = SystemClock.elapsedRealtimeNanos() / 1_000_000L
                - ubicacion.getElapsedRealtimeNanos() / 1_000_000L;
        if (edadMs < 0 || edadMs > MAX_EDAD_UBICACION_MS)
            return Resultado.error("Ubicación antigua: esperar otra posición.");
        if (!ubicacion.hasAccuracy() || (!Float.isFinite(ubicacion.getAccuracy()) || ubicacion.getAccuracy() < 0 || ubicacion.getAccuracy() > MAX_PRECISION_METROS))
            return Resultado.error("Ubicación imprecisa: esperar mejor señal.");
        double lat = ubicacion.getLatitude(), lon = ubicacion.getLongitude();
        if (!Double.isFinite(lat) || !Double.isFinite(lon) || lat < -90 || lat > 90 || lon < -180 || lon > 180)
            return Resultado.error("Coordenadas inválidas.");
        long ahora = SystemClock.elapsedRealtime();
        if (!contadores.aceptar(seleccionado.toUpperCase(Locale.ROOT), trama.contador, ahora))
            return Resultado.error("Anuncio BLE repetido (contador " + trama.contador + ").");
        // El instante representa recepción BLE, no la adquisición real del sensor.
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ROOT);
        formato.setTimeZone(TimeZone.getTimeZone("UTC"));
        MedicionAmbiental medicion = new MedicionAmbiental(seleccionado, trama.minor, lat, lon,
                ubicacion.getAccuracy(), formato.format(new Date()), trama.contador);
        return Resultado.ok(medicion);
    }

    /** Diseño lógico: () -> reiniciar() -> (void)
     * Descripción: Limpia los controles de duplicados al comenzar otra prueba. */
    public void reiniciar() { contadores.reiniciar(); }

    /** Resultado con medición o motivo de descarte. */
    public static final class Resultado {
        public final MedicionAmbiental medicion;
        public final String mensaje;
        private Resultado(MedicionAmbiental medicion, String mensaje) {
            this.medicion = medicion;
            this.mensaje = mensaje;
        }
        /** Diseño lógico: (MedicionAmbiental) -> ok() -> (Resultado) */
        public static Resultado ok(MedicionAmbiental medicion) { return new Resultado(medicion, "Medición preparada (NO enviada)."); }
        /** Diseño lógico: (String) -> error() -> (Resultado) */
        public static Resultado error(String motivo) { return new Resultado(null, motivo); }
    }
}
