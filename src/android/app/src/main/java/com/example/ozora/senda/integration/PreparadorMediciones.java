/*
 * Nombre: PreparadorMediciones.java
 * Descripción: Valida ubicación y deduplica lecturas de ozono.
 * Copyright: (c) 2026 Equipo 2 - SENDA / OZORA.
 * Fecha: 02/11/2026
 * Autores: Fabián Andrés Useche Rivera; Élia Garcia Pons; Judith Civico Aporta; Julia Valen de Oliveira; Soufiane Belfquih El Khalfi
 * Aportación: Valida ubicación y deduplica lecturas de ozono.
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
    private final ControlLecturasSinContador sinContador = new ControlLecturasSinContador();



    /** --------------------
     * Diseño lógico: seleccionado: Text, trama: IBeaconParserResultado, ubicacion: Posicion --> preparar() -->
     * Resultado <--
     * Descripción: Rechaza otra emisora, tramas no O3, ubicación antigua o imprecisa y duplicados recientes
     * -------------------- */
    public Resultado preparar(String seleccionado, IBeaconParser.Resultado trama, Location ubicacion) {
        if (seleccionado == null || trama == null || trama.nombreBle == null
                || !seleccionado.equalsIgnoreCase(trama.nombreBle)) {
            return Resultado.error("La trama no corresponde al llavero seleccionado.");
        }
        if (!trama.esOzono()) return Resultado.error("La trama no contiene O3.");
        if (trama.obtenerOzonoPpb() == null) return Resultado.error("Lectura O3 no disponible.");
        String errorGps = errorUbicacion(ubicacion);
        if (errorGps != null) return Resultado.error(errorGps);
        double lat = ubicacion.getLatitude(), lon = ubicacion.getLongitude();
        long ahora = SystemClock.elapsedRealtime();
        String identidad = seleccionado.toUpperCase(Locale.ROOT);
        // El backend conserva contador; en el protocolo nuevo es una secuencia LOCAL de recepcion.
        int contador = trama.contador;
        if (contador < 0) contador = sinContador.aceptar(identidad, ahora);
        else if (!contadores.aceptar(identidad, contador, ahora)) contador = -1;
        if (contador < 0) return Resultado.error("Anuncio BLE repetido.");
        // El instante representa recepción BLE, no la adquisición real del sensor.
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ROOT);
        formato.setTimeZone(TimeZone.getTimeZone("UTC"));
        MedicionAmbiental medicion = new MedicionAmbiental(seleccionado, trama.minor, lat, lon,
                ubicacion.getAccuracy(), formato.format(new Date()), contador);
        return Resultado.ok(medicion);
    }


    /** --------------------
     * Diseño lógico: ubicacion: Posicion --> errorUbicacion() --x
     * Text <--
     * Descripción: Error Ubicacion. Valida ubicación y deduplica lecturas de ozono
     * -------------------- */
    static String errorUbicacion(Location ubicacion) {
        if (ubicacion == null) return "Esperando la primera ubicación.";
        long edadMs = SystemClock.elapsedRealtimeNanos() / 1_000_000L
                - ubicacion.getElapsedRealtimeNanos() / 1_000_000L;
        if (edadMs < 0 || edadMs > MAX_EDAD_UBICACION_MS)
            return "Ubicación antigua: esperar otra posición.";
        if (!ubicacion.hasAccuracy() || (!Float.isFinite(ubicacion.getAccuracy()) || ubicacion.getAccuracy() < 0 || ubicacion.getAccuracy() > MAX_PRECISION_METROS))
            return "Ubicación imprecisa: esperar mejor señal.";
        double lat = ubicacion.getLatitude(), lon = ubicacion.getLongitude();
        if (!Double.isFinite(lat) || !Double.isFinite(lon) || lat < -90 || lat > 90 || lon < -180 || lon > 180)
            return "Coordenadas inválidas.";
        return null;
    }



    /** --------------------
     * Diseño lógico: reiniciar() -->
     * Descripción: Limpia los controles de duplicados al comenzar otra prueba
     * -------------------- */
    public void reiniciar() { contadores.reiniciar(); sinContador.reiniciar(); }

    /** Resultado con medición o motivo de descarte. */
    public static final class Resultado {
        public final MedicionAmbiental medicion;
        public final String mensaje;

        /** --------------------
         * Diseño lógico: medicion: MedicionAmbiental, mensaje: Text --> Resultado() -->
         * Resultado <--
         * Descripción: Construye Resultado. Aplicación de campo: acceso, captura, recorridos y presentación Android
         * -------------------- */
        private Resultado(MedicionAmbiental medicion, String mensaje) {
            this.medicion = medicion;
            this.mensaje = mensaje;
        }


        /** --------------------
         * Diseño lógico: medicion: MedicionAmbiental --> ok() --x
         * Resultado <--
         * Descripción: Ok. Aplicación de campo: acceso, captura, recorridos y presentación Android
         * -------------------- */
        public static Resultado ok(MedicionAmbiental medicion) { return new Resultado(medicion, "Medición preparada (NO enviada)."); }


        /** --------------------
         * Diseño lógico: motivo: Text --> error() --x
         * Resultado <--
         * Descripción: Error. Aplicación de campo: acceso, captura, recorridos y presentación Android
         * -------------------- */
        public static Resultado error(String motivo) { return new Resultado(null, motivo); }
    }
}
