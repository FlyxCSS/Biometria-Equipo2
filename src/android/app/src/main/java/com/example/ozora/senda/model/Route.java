/*
 * Nombre: Route.java
 * Descripción: Aplicación de campo: acceso, captura, recorridos y presentación Android.
 * Copyright: (c) 2026 Equipo 2 - SENDA / OZORA.
 * Fecha: 19/11/2026
 * Autores: Fabián Andrés Useche Rivera; Élia Garcia Pons; Judith Civico Aporta; Julia Valen de Oliveira; Soufiane Belfquih El Khalfi
 * Aportación: Aplicación de campo: acceso, captura, recorridos y presentación Android.
 */
package com.example.ozora.senda.model;

public final class Route {
    public final long id, start;
    public final Long end;
    public final double meters, mean;
    public final String sensor;
    public final int count, minimum, maximum;


    /** --------------------
     * Diseño lógico: id: Z, start: Z, end: Z, meters: R, sensor: Text, count: Z, mean: R, minimum: Z, maximum: Z --> Route() -->
     * Route <--
     * Descripción: Construye Route. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    public Route(long id, long start, Long end, double meters, String sensor, int count,
                 double mean, int minimum, int maximum) {
        this.id = id;
        this.start = start;
        this.end = end;
        this.meters = meters;
        this.sensor = sensor;
        this.count = count;
        this.mean = mean;
        this.minimum = minimum;
        this.maximum = maximum;
    }
}
