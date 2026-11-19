/*
 * Nombre: KeyringStatusTest.java
 * Descripción: Aplicación de campo: acceso, captura, recorridos y presentación Android.
 * Copyright: (c) 2026 Equipo 2 - SENDA / OZORA.
 * Fecha: 19/11/2026
 * Autores: Fabián Andrés Useche Rivera; Élia Garcia Pons; Judith Civico Aporta; Julia Valen de Oliveira; Soufiane Belfquih El Khalfi
 * Aportación: Aplicación de campo: acceso, captura, recorridos y presentación Android.
 */
package com.example.ozora.senda.ui;
import org.junit.Test;
import static org.junit.Assert.*;
public class KeyringStatusTest {

    /** --------------------
     * Diseño lógico: requiereRecorridoYAnuncioDelServicioActual() -->
     * Descripción: Requiere Recorrido YAnuncio Del Servicio Actual. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    @Test public void requiereRecorridoYAnuncioDelServicioActual() {
        assertTrue(KeyringStatus.receiving(true, 7, 7, 1000, 1500, 20));
        assertFalse(KeyringStatus.receiving(false, 7, 7, 1000, 1500, 20));
        assertFalse(KeyringStatus.receiving(true, 7, 8, 1000, 1500, 20));
        assertFalse(KeyringStatus.receiving(true, 7, 7, 0, 1500, 20));
        assertFalse(KeyringStatus.receiving(true, 0, 0, 1000, 1500, 20));
    }

    /** --------------------
     * Diseño lógico: elSilencioCaducaEnElLimiteYDescartaRelojesIncompatibles() -->
     * Descripción: El Silencio Caduca En El Limite YDescarta Relojes Incompatibles. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    @Test public void elSilencioCaducaEnElLimiteYDescartaRelojesIncompatibles() {
        assertTrue(KeyringStatus.receiving(true, 7, 7, 1000, 20999, 20));
        assertFalse(KeyringStatus.receiving(true, 7, 7, 1000, 21000, 20));
        assertFalse(KeyringStatus.receiving(true, 7, 7, 2000, 1000, 20));
    }
}
