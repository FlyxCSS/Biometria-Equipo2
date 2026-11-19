/*
 * Nombre: PreparadorTemperaturasTest.java
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
import com.example.ozora.senda.integration.PreparadorTemperaturas;
import com.example.ozora.senda.model.MedicionTemperatura;
import org.junit.Test;
import static org.junit.Assert.*;

public class PreparadorTemperaturasTest {

    /** --------------------
     * Diseño lógico: valor: Z --> trama() -->
     * IBeaconParserResultado <--
     * Descripción: Trama. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    private IBeaconParser.Resultado trama(int valor) {
        byte[] b=new byte[23]; b[0]=2; b[1]=0x15;
        String hex=IBeaconParser.UUID_PROYECTO.replace("-", "");
        for(int i=0;i<16;i++) b[i+2]=(byte)Integer.parseInt(hex.substring(i*2,i*2+2),16);
        b[18]=12; b[19]=45; b[20]=(byte)(valor>>8); b[21]=(byte)valor;
        return IBeaconParser.interpretar(b,"S-001",-70);
    }

    /** --------------------
     * Diseño lógico: ubicacion() -->
     * Posicion <--
     * Descripción: Ubicacion. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    private Location ubicacion() {
        Location p=new Location("test"); p.setLatitude(39.123456789); p.setLongitude(-0.376312345);
        p.setAccuracy(5); p.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos()); return p;
    }

    /** --------------------
     * Diseño lógico: esperaGpsYConservaLaPosicionDeLaMuestraSinDuplicarla() -->
     * Descripción: Espera Gps YConserva La Posicion De La Muestra Sin Duplicarla. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    @Test public void esperaGpsYConservaLaPosicionDeLaMuestraSinDuplicarla() {
        PreparadorTemperaturas preparador=new PreparadorTemperaturas();
        assertNull(preparador.preparar("S-001",trama(2939),null));
        Location gps=ubicacion();
        MedicionTemperatura m=preparador.preparar("S-001",trama(2939),gps);
        assertNotNull(m); assertEquals(2939,m.centesimas);
        assertEquals(gps.getLatitude(),m.latitud,0); assertEquals(gps.getLongitude(),m.longitud,0);
        assertNull(preparador.preparar("S-001",trama(2939),gps));
        preparador.reiniciar();
        assertEquals(-525,preparador.preparar("S-001",trama(-525),gps).centesimas);
    }

    /** --------------------
     * Diseño lógico: rechazaGpsAntiguoImprecisoInvalidoYOtroLlavero() -->
     * Descripción: Rechaza Gps Antiguo Impreciso Invalido YOtro Llavero. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    @Test public void rechazaGpsAntiguoImprecisoInvalidoYOtroLlavero() {
        PreparadorTemperaturas preparador=new PreparadorTemperaturas();
        Location gps=ubicacion(); gps.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos()-11_000_000_000L);
        assertNull(preparador.preparar("S-001",trama(2939),gps));
        gps=ubicacion(); gps.setAccuracy(101);
        assertNull(preparador.preparar("S-001",trama(2939),gps));
        gps=ubicacion(); gps.setLatitude(Double.NaN);
        assertNull(preparador.preparar("S-001",trama(2939),gps));
        assertNull(preparador.preparar("S-002",trama(2939),ubicacion()));
        assertNull(preparador.preparar("S-001",trama(0x8000),ubicacion()));
        assertNotNull(preparador.preparar("S-001",trama(2939),ubicacion()));
    }
}
