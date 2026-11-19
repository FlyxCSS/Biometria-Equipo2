/*
 * Nombre: RoutePoint.java
 * Descripción: Aplicación de campo: acceso, captura, recorridos y presentación Android.
 * Copyright: (c) 2026 Equipo 2 - SENDA / OZORA.
 * Fecha: 19/11/2026
 * Autores: Fabián Andrés Useche Rivera; Élia Garcia Pons; Judith Civico Aporta; Julia Valen de Oliveira; Soufiane Belfquih El Khalfi
 * Aportación: Aplicación de campo: acceso, captura, recorridos y presentación Android.
 */
package com.example.ozora.senda.model;

public final class RoutePoint {
    public final double latitude, longitude;
    public final long segment;


    /** --------------------
     * Diseño lógico: latitude: R, longitude: R, segment: Z --> RoutePoint() -->
     * RoutePoint <--
     * Descripción: Construye RoutePoint. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    public RoutePoint(double latitude, double longitude, long segment) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.segment = segment;
    }
}
