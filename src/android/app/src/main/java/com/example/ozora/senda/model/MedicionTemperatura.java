/*
 * Nombre: MedicionTemperatura.java
 * Descripción: Representa temperatura en centésimas con ubicación.
 * Copyright: (c) 2026 Equipo 2 - SENDA / OZORA.
 * Fecha: 19/11/2026
 * Autores: Fabián Andrés Useche Rivera; Élia Garcia Pons; Judith Civico Aporta; Julia Valen de Oliveira; Soufiane Belfquih El Khalfi
 * Aportación: Representa temperatura en centésimas con ubicación.
 */
package com.example.ozora.senda.model;

/** Temperatura BLE en centésimas con la ubicación validada de su recepción. */
public final class MedicionTemperatura {
    public final String idLlavero;
    public final int centesimas;
    public final String fechaIso8601;
    public final double latitud, longitud;


    /** --------------------
     * Diseño lógico: idLlavero: Text, centesimas: Z, fechaIso8601: Text, latitud: R, longitud: R --> MedicionTemperatura() -->
     * MedicionTemperatura <--
     * Descripción: Construye MedicionTemperatura. Representa temperatura en centésimas con ubicación
     * -------------------- */
    public MedicionTemperatura(String idLlavero, int centesimas, String fechaIso8601,
                              double latitud, double longitud) {
        this.idLlavero = idLlavero;
        this.centesimas = centesimas;
        this.fechaIso8601 = fechaIso8601;
        this.latitud = latitud;
        this.longitud = longitud;
    }
}
