/*
 * Nombre: IBeaconParserTest.java
 * Descripción: Aplicación de campo: acceso, captura, recorridos y presentación Android.
 * Copyright: (c) 2026 Equipo 2 - SENDA / OZORA.
 * Fecha: 19/11/2026
 * Autores: Fabián Andrés Useche Rivera; Élia Garcia Pons; Judith Civico Aporta; Julia Valen de Oliveira; Soufiane Belfquih El Khalfi
 * Aportación: Aplicación de campo: acceso, captura, recorridos y presentación Android.
 */
package com.example.ozora.senda.bluetooth;
import org.junit.Test;
import static org.junit.Assert.*;
public class IBeaconParserTest {

    /** --------------------
     * Diseño lógico: trama() -->
     * [Dato] <--
     * Descripción: Trama. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    private byte[] trama() {
        byte[] b=new byte[23]; b[0]=2; b[1]=0x15;
        String hex=IBeaconParser.UUID_PROYECTO.replace("-","");
        for(int i=0;i<16;i++) b[i+2]=(byte)Integer.parseInt(hex.substring(i*2,i*2+2),16);
        b[18]=11; b[19]=(byte)255; b[20]=1; b[21]=(byte)244; b[22]=-59;
        return b;
    }

    /** --------------------
     * Diseño lógico: conservaIdentidadContadorYUnidades() -->
     * Descripción: Conserva Identidad Contador YUnidades. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    @Test public void conservaIdentidadContadorYUnidades() {
        IBeaconParser.Resultado r=IBeaconParser.interpretar(trama(),"S-001",-70);
        assertNotNull(r); assertEquals("S-001",r.nombreBle); assertTrue(r.esOzono());
        assertEquals(255,r.contador); assertEquals(500,r.minor); assertEquals(0.5,r.obtenerOzonoPpm(),0);
    }

    /** --------------------
     * Diseño lógico: rechazaUuidFormatoYLongitudAjenos() -->
     * Descripción: Rechaza Uuid Formato YLongitud Ajenos. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    @Test public void rechazaUuidFormatoYLongitudAjenos() {
        byte[] b=trama(); b[2]^=1; assertNull(IBeaconParser.interpretar(b,"S-001",-70));
        b=trama(); b[1]=0; assertNull(IBeaconParser.interpretar(b,"S-001",-70));
        assertNull(IBeaconParser.interpretar(new byte[22],"S-001",-70));
    }

    /** --------------------
     * Diseño lógico: noInventaNombreNiConvierteOtroTipoEnOzono() -->
     * Descripción: No Inventa Nombre Ni Convierte Otro Tipo En Ozono. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    @Test public void noInventaNombreNiConvierteOtroTipoEnOzono() {
        byte[] b=trama(); b[18]=12;
        IBeaconParser.Resultado r=IBeaconParser.interpretar(b,null,-70);
        assertNotNull(r); assertNull(r.nombreBle); assertFalse(r.esOzono()); assertNull(r.obtenerOzonoPpb());
    }
}
