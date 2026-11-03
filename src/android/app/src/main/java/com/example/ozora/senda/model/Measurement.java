/*
 * Nombre: Measurement.java
 * Descripción: Modelo inmutable de O3 con geolocalización y fecha.
 * Copyright (c) 2026 - Equipo 2 - SENDA / OZORA
 * Fecha: 02/11/2026
 * Autor: ...
 * Aportación: Preparación local de datos para integración REST, sin transmisión.
 */
package com.example.ozora.senda.model;

import java.util.Locale;

/** Datos preparados para REST: no implican aceptación ni envío al servidor. */
public class Measurement {
    public final String idLlavero;
    public final String tipo;
    public final int valorPpb;
    public final double latitud;
    public final double longitud;
    public final float precisionMetros;
    public final String fechaIso8601;
    public final int contador;

    /** Diseño lógico: (String, int, double, double, float, String, int) -> Measurement() -> (Measurement)
     * Descripción: Construye una lectura inmutable con unidad ppb. */
    public Measurement(String idLlavero, int valorPpb, double latitud, double longitud,
                             float precisionMetros, String fechaIso8601, int contador) {
        this.idLlavero = idLlavero;
        this.tipo = "O3";
        this.valorPpb = valorPpb;
        this.latitud = latitud;
        this.longitud = longitud;
        this.precisionMetros = precisionMetros;
        this.fechaIso8601 = fechaIso8601;
        this.contador = contador;
    }

    /** Diseño lógico: () -> convertirAPpm() -> (double)
     * Descripción: Obtiene la equivalencia para la interfaz; se conservan ppb en el modelo. */
    public double convertirAPpm() { return valorPpb / 1000.0; }

    /** Diseño lógico: () -> resumen() -> (String)
     * Descripción: Resumen local; nunca imprime credenciales. */
    public String resumen() {
        return String.format(Locale.ROOT, "%s: %d ppb (%.3f ppm) | %.6f, %.6f | ±%.1f m | %s",
                idLlavero, valorPpb, convertirAPpm(), latitud, longitud, precisionMetros, fechaIso8601);
    }
}
