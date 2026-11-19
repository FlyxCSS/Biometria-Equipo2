/*
 * Nombre: Worker.java
 * Descripción: Aplicación de campo: acceso, captura, recorridos y presentación Android.
 * Copyright: (c) 2026 Equipo 2 - SENDA / OZORA.
 * Fecha: 19/11/2026
 * Autores: Fabián Andrés Useche Rivera; Élia Garcia Pons; Judith Civico Aporta; Julia Valen de Oliveira; Soufiane Belfquih El Khalfi
 * Aportación: Aplicación de campo: acceso, captura, recorridos y presentación Android.
 */
package com.example.ozora.senda.model;

public final class Worker {
    public final int id, parkId;
    public final String name;


    /** --------------------
     * Diseño lógico: id: Z, parkId: Z, name: Text --> Worker() -->
     * Worker <--
     * Descripción: Construye Worker. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    public Worker(int id, int parkId, String name) {
        this.id = id;
        this.parkId = parkId;
        this.name = name;
    }
}
