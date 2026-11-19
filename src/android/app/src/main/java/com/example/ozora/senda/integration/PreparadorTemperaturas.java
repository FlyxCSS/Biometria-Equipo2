/*
 * Nombre: PreparadorTemperaturas.java
 * Descripción: Valida ubicación y deduplica temperatura de forma independiente.
 * Copyright: (c) 2026 Equipo 2 - SENDA / OZORA.
 * Fecha: 19/11/2026
 * Autores: Fabián Andrés Useche Rivera; Élia Garcia Pons; Judith Civico Aporta; Julia Valen de Oliveira; Soufiane Belfquih El Khalfi
 * Aportación: Valida ubicación y deduplica temperatura de forma independiente.
 */
package com.example.ozora.senda.integration;

import android.location.Location;
import android.os.SystemClock;
import com.example.ozora.senda.bluetooth.IBeaconParser;
import com.example.ozora.senda.model.MedicionTemperatura;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/** Valida GPS y deduplica temperatura independientemente del contador de O3. */
public final class PreparadorTemperaturas {
    private final ControlContadores contadores = new ControlContadores();
    private final ControlLecturasSinContador sinContador = new ControlLecturasSinContador();


    /** --------------------
     * Diseño lógico: sensor: Text, trama: IBeaconParserResultado, ubicacion: Posicion --> preparar() -->
     * MedicionTemperatura <--
     * Descripción: Preparar. Valida ubicación y deduplica temperatura de forma independiente
     * -------------------- */
    public MedicionTemperatura preparar(String sensor, IBeaconParser.Resultado trama, Location ubicacion) {
        if (sensor == null || trama == null || !sensor.equalsIgnoreCase(trama.nombreBle)
                || trama.obtenerTemperaturaC() == null
                || PreparadorMediciones.errorUbicacion(ubicacion) != null) return null;
        String identidad = sensor.toUpperCase(Locale.ROOT);
        long ahora = SystemClock.elapsedRealtime();
        boolean aceptada = trama.contador < 0 ? sinContador.aceptar(identidad, ahora) >= 0
                : contadores.aceptar(identidad, trama.contador, ahora);
        if (!aceptada) return null;
        SimpleDateFormat fecha = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ROOT);
        fecha.setTimeZone(TimeZone.getTimeZone("UTC"));
        return new MedicionTemperatura(sensor, (short) trama.minor, fecha.format(new Date()),
                ubicacion.getLatitude(), ubicacion.getLongitude());
    }


    /** --------------------
     * Diseño lógico: reiniciar() -->
     * Descripción: Reiniciar. Valida ubicación y deduplica temperatura de forma independiente
     * -------------------- */
    public void reiniciar() { contadores.reiniciar(); sinContador.reiniciar(); }
}
