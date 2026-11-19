/*
 * Nombre: PreparadorMedicionesTest.java
 * Descripción: Aplicación de campo: acceso, captura, recorridos y presentación Android.
 * Copyright: (c) 2026 Equipo 2 - SENDA / OZORA.
 * Fecha: 19/11/2026
 * Autores: Fabián Andrés Useche Rivera; Élia Garcia Pons; Judith Civico Aporta; Julia Valen de Oliveira; Soufiane Belfquih El Khalfi
 * Aportación: Aplicación de campo: acceso, captura, recorridos y presentación Android.
 */
package com.example.ozora.senda;

import android.location.Location;
import android.os.SystemClock;
import com.example.ozora.senda.bluetooth.IBeaconParser;
import com.example.ozora.senda.integration.PreparadorMediciones;
import org.junit.Test;
import static org.junit.Assert.*;

public class PreparadorMedicionesTest {

    /** --------------------
     * Diseño lógico: aceptaMasDeCuatroLecturasIgualesConPosicionRecienteSinMoverse() -->
     * Descripción: Acepta Mas De Cuatro Lecturas Iguales Con Posicion Reciente Sin Moverse. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    @Test public void aceptaMasDeCuatroLecturasIgualesConPosicionRecienteSinMoverse() {
        PreparadorMediciones preparador = new PreparadorMediciones();
        Location posicion = new Location("test");
        posicion.setLatitude(39);
        posicion.setLongitude(-0.1);
        posicion.setAccuracy(5);
        byte[] trama = new byte[23];
        trama[0] = 2; trama[1] = 0x15;
        String hex = IBeaconParser.UUID_PROYECTO.replace("-", "");
        for (int i=0; i<16; i++) trama[i+2] = (byte)Integer.parseInt(hex.substring(i*2,i*2+2),16);
        trama[18] = 11; trama[21] = 33; trama[22] = -59;
        for (int contador=0; contador<8; contador++) {
            trama[19] = (byte)contador;
            posicion.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
            PreparadorMediciones.Resultado resultado = preparador.preparar("S-001",
                    IBeaconParser.interpretar(trama,"S-001",-70), posicion);
            assertNotNull(resultado.mensaje, resultado.medicion);
            assertEquals(33, resultado.medicion.valorPpb);
        }
        posicion.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos()-11_000_000_000L);
        trama[19] = 9;
        PreparadorMediciones.Resultado antigua = preparador.preparar("S-001",
                IBeaconParser.interpretar(trama,"S-001",-70), posicion);
        assertNull(antigua.medicion);
        assertTrue(antigua.mensaje.contains("antigua"));
    }
}
