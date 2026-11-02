/* Nombre: MedicionAmbientalTest.java | Descripción: Conversión ppb ppm | Fecha: 02/11/2026 | Autor: ... */
package com.example.ozora.senda.model;
import org.junit.Test;
import static org.junit.Assert.*;
public class MedicionAmbientalTest {
    /** Diseño lógico: () -> convierteAConcentracionPpm() -> (void). */
    @Test public void convierteAConcentracionPpm() {
        MedicionAmbiental m = new MedicionAmbiental("S-001",2,38.99,-0.16,15f,"2026-11-02T22:00:00.000Z",7);
        assertEquals(0.002,m.convertirAPpm(),0.000001);
        assertEquals("O3",m.tipo);
        assertEquals(2,m.valorPpb);
    }
}
