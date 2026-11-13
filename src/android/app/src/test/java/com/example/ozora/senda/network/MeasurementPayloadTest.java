/*
 * Nombre: MeasurementPayloadTest.java
 * Descripción: Pruebas del contrato JSON compartido con el backend.
 * Copyright: (c) 2026 Equipo 2 - SENDA / OZORA.
 * Fecha: 03/11/2026.
 * Autor: Julia (con asistencia de Codex).
 * Aportación: Evitar pérdida de coordenadas o envío de campos ajenos al contrato.
 */
package com.example.ozora.senda.network;

import com.example.ozora.senda.model.MedicionAmbiental;
import java.util.Map;
import org.junit.Test;
import static org.junit.Assert.*;

public class MeasurementPayloadTest {
    /** Diseño lógico: () -> contratoConservaFechaUnidadYPrecision -> vacío.
     * Descripción: Comprueba el contrato real, sin contador ni precisión GPS adicionales. */
    @Test public void contratoConservaFechaUnidadYPrecision() {
        MedicionAmbiental m=new MedicionAmbiental("S-001",123,39.1234567890123,-0.1234567890123,5,"2026-11-03T12:00:00.123Z",17);
        Map<String,Object> d=MeasurementPayload.datos(m);
        assertEquals(8,d.size()); assertEquals("S-001",d.get("nombre_micro"));
        assertEquals("O3",d.get("tipo")); assertEquals("ppb",d.get("unidad"));
        assertEquals(123,d.get("valor")); assertEquals(m.latitud,(Double)d.get("latitud"),0);
        assertEquals(m.longitud,(Double)d.get("longitud"),0); assertEquals(m.fechaIso8601,d.get("fecha_hora"));
        assertEquals(false,d.get("simulada")); assertFalse(d.containsKey("contador"));
    }
    /** Diseño lógico: () -> rechazaCoordenadaNoFinita -> vacío.
     * Descripción: No deja construir JSON con valores GPS no representables. */
    @Test(expected=IllegalArgumentException.class) public void rechazaCoordenadaNoFinita() {
        MeasurementPayload.datos(new MedicionAmbiental("S-001",1,Double.NaN,0,5,"fecha",1));
    }
}
