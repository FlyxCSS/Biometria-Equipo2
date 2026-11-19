/*
 * Nombre: ControlContadoresTest.java
 * Descripción: Aplicación de campo: acceso, captura, recorridos y presentación Android.
 * Copyright: (c) 2026 Equipo 2 - SENDA / OZORA.
 * Fecha: 02/11/2026
 * Autores: Fabián Andrés Useche Rivera; Élia Garcia Pons; Judith Civico Aporta; Julia Valen de Oliveira; Soufiane Belfquih El Khalfi
 * Aportación: Aplicación de campo: acceso, captura, recorridos y presentación Android.
 */
package com.example.ozora.senda.integration;
import org.junit.Test;
import static org.junit.Assert.*;
public class ControlContadoresTest {


    /** --------------------
     * Diseño lógico: rechazaAnuncioInmediato() -->
     * Descripción: Rechaza Anuncio Inmediato. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    @Test public void rechazaAnuncioInmediato() {
        ControlContadores c = new ControlContadores();
        assertTrue(c.aceptar("S-001", 12, 10000));
        assertFalse(c.aceptar("S-001", 12, 11000));
        assertTrue(c.aceptar("S-001", 13, 11000));
    }


    /** --------------------
     * Diseño lógico: permiteVueltaYReinicio() -->
     * Descripción: Permite Vuelta YReinicio. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    @Test public void permiteVueltaYReinicio() {
        ControlContadores c = new ControlContadores();
        assertTrue(c.aceptar("S-001", 255, 10000));
        assertTrue(c.aceptar("S-001", 0, 15000));
        assertTrue(c.aceptar("S-001", 0, 20000));
        c.reiniciar();
        assertTrue(c.aceptar("S-001", 0, 21000));
    }


    /** --------------------
     * Diseño lógico: separaDispositivos() -->
     * Descripción: Separa Dispositivos. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    @Test public void separaDispositivos() {
        ControlContadores c = new ControlContadores();
        assertTrue(c.aceptar("S-001", 1, 10000));
        assertTrue(c.aceptar("S-002", 1, 10001));
    }
}
