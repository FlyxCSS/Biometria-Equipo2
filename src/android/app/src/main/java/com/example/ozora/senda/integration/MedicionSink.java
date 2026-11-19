/*
 * Nombre: MedicionSink.java
 * Descripción: Aplicación de campo: acceso, captura, recorridos y presentación Android.
 * Copyright: (c) 2026 Equipo 2 - SENDA / OZORA.
 * Fecha: 02/11/2026
 * Autores: Fabián Andrés Useche Rivera; Élia Garcia Pons; Judith Civico Aporta; Julia Valen de Oliveira; Soufiane Belfquih El Khalfi
 * Aportación: Aplicación de campo: acceso, captura, recorridos y presentación Android.
 */
package com.example.ozora.senda.integration;

import com.example.ozora.senda.model.MedicionAmbiental;

public interface MedicionSink {


    /** --------------------
     * Diseño lógico: medicion: MedicionAmbiental --> enviar() -->
     * Descripción: Implementación futura: transmisión SOLO tras autorización y sesión válida
     * -------------------- */
    void enviar(MedicionAmbiental medicion);
}
