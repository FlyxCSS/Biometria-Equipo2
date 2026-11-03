/* Nombre: ControlContadoresTest.java | Descripción: Tests de deduplicación | Fecha: 02/11/2026 | Autor: ... */
package com.example.ozora.senda.integration;
import org.junit.Test;
import static org.junit.Assert.*;
public class ControlContadoresTest {
    /** Diseño lógico: () -> rechazaAnuncioInmediato() -> (void). */
    @Test public void rechazaAnuncioInmediato() {
        ControlContadores c = new ControlContadores();
        assertTrue(c.aceptar("S-001", 12, 10000));
        assertFalse(c.aceptar("S-001", 12, 11000));
        assertTrue(c.aceptar("S-001", 13, 11000));
    }
    /** Diseño lógico: () -> permiteVueltaYReinicio() -> (void). */
    @Test public void permiteVueltaYReinicio() {
        ControlContadores c = new ControlContadores();
        assertTrue(c.aceptar("S-001", 255, 10000));
        assertTrue(c.aceptar("S-001", 0, 15000));
        assertTrue(c.aceptar("S-001", 0, 20000));
        c.reiniciar();
        assertTrue(c.aceptar("S-001", 0, 21000));
    }
    /** Diseño lógico: () -> separaDispositivos() -> (void). */
    @Test public void separaDispositivos() {
        ControlContadores c = new ControlContadores();
        assertTrue(c.aceptar("S-001", 1, 10000));
        assertTrue(c.aceptar("S-002", 1, 10001));
    }
}
