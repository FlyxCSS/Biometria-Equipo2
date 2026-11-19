/*
 * Nombre: LogicaFakePayloadTest.java
 * Descripción: Aplicación de campo: acceso, captura, recorridos y presentación Android.
 * Copyright: (c) 2026 Equipo 2 - SENDA / OZORA.
 * Fecha: 03/11/2026.
 * Autores: Fabián Andrés Useche Rivera; Élia Garcia Pons; Judith Civico Aporta; Julia Valen de Oliveira; Soufiane Belfquih El Khalfi
 * Aportación: Aplicación de campo: acceso, captura, recorridos y presentación Android.
 */
package com.example.ozora.senda.network;

import com.example.ozora.senda.model.MedicionAmbiental;
import com.example.ozora.senda.model.MedicionTemperatura;
import java.util.Map;
import org.junit.Test;
import static org.junit.Assert.*;

public class LogicaFakePayloadTest {

    /** --------------------
     * Diseño lógico: temperaturaSeEnviaEnGradosConSignoYCoordenadas() -->
     * Descripción: Temperatura Se Envia En Grados Con Signo YCoordenadas. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    @Test public void temperaturaSeEnviaEnGradosConSignoYCoordenadas() throws Exception {
        for (int centesimas : new int[]{2939,-525,-4000,0,5000}) {
            Map<String,Object> d=LogicaFake.datosTemperatura(new MedicionTemperatura(
                    "S-001",centesimas,"2026-11-19T10:00:00.000Z",39.123456789,-0.376312345));
            org.json.JSONObject json=new org.json.JSONObject(d);
            assertEquals(8,json.length());
            assertEquals("TEMPERATURA",json.getString("tipo"));
            assertEquals("C",json.getString("unidad"));
            assertEquals(centesimas/100.0,json.getDouble("valor"),0.000001);
            assertEquals(39.123456789,json.getDouble("latitud"),0);
            assertEquals(-0.376312345,json.getDouble("longitud"),0);
        }
    }


    /** --------------------
     * Diseño lógico: noEnviaTemperaturaNoDisponible() -->
     * Descripción: No Envia Temperatura No Disponible. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    @Test(expected=IllegalArgumentException.class) public void noEnviaTemperaturaNoDisponible() {
        LogicaFake.datosTemperatura(new MedicionTemperatura("S-001",-32768,"fecha",39,-0.1));
    }


    /** --------------------
     * Diseño lógico: rechazaTemperaturaSinCoordenadasValidas() -->
     * Descripción: Rechaza Temperatura Sin Coordenadas Validas. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    @Test(expected=IllegalArgumentException.class) public void rechazaTemperaturaSinCoordenadasValidas() {
        LogicaFake.datosTemperatura(new MedicionTemperatura("S-001",2939,"fecha",Double.NaN,-0.3));
    }


    /** --------------------
     * Diseño lógico: contratoConservaFechaUnidadYPrecision() -->
     * Descripción: Comprueba el contrato real, sin contador ni precisión GPS adicionales
     * -------------------- */
    @Test public void contratoConservaFechaUnidadYPrecision() {
        MedicionAmbiental m=new MedicionAmbiental("S-001",123,39.1234567890123,-0.1234567890123,5,"2026-11-03T12:00:00.123Z",17);
        Map<String,Object> d=LogicaFake.datosMedicion(m);
        assertEquals(8,d.size()); assertEquals("S-001",d.get("nombre_micro"));
        assertEquals("O3",d.get("tipo")); assertEquals("ppb",d.get("unidad"));
        assertEquals(123,d.get("valor")); assertEquals(m.latitud,(Double)d.get("latitud"),0);
        assertEquals(m.longitud,(Double)d.get("longitud"),0); assertEquals(m.fechaIso8601,d.get("fecha_hora"));
        assertEquals(false,d.get("simulada")); assertFalse(d.containsKey("contador"));
    }


    /** --------------------
     * Diseño lógico: rechazaCoordenadaNoFinita() -->
     * Descripción: No deja construir JSON con valores GPS no representables
     * -------------------- */
    @Test(expected=IllegalArgumentException.class) public void rechazaCoordenadaNoFinita() {
        LogicaFake.datosMedicion(new MedicionAmbiental("S-001",1,Double.NaN,0,5,"fecha",1));
    }
}
