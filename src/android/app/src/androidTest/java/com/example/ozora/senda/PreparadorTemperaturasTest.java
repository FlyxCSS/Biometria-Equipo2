package com.example.ozora.senda;

import android.location.Location;
import android.os.SystemClock;
import com.example.ozora.senda.bluetooth.IBeaconParser;
import com.example.ozora.senda.integration.PreparadorTemperaturas;
import com.example.ozora.senda.model.MedicionTemperatura;
import org.junit.Test;
import static org.junit.Assert.*;

public class PreparadorTemperaturasTest {
    private IBeaconParser.Resultado trama(int valor) {
        byte[] b=new byte[23]; b[0]=2; b[1]=0x15;
        String hex=IBeaconParser.UUID_PROYECTO.replace("-", "");
        for(int i=0;i<16;i++) b[i+2]=(byte)Integer.parseInt(hex.substring(i*2,i*2+2),16);
        b[18]=12; b[19]=45; b[20]=(byte)(valor>>8); b[21]=(byte)valor;
        return IBeaconParser.interpretar(b,"S-001",-70);
    }
    private Location ubicacion() {
        Location p=new Location("test"); p.setLatitude(39.123456789); p.setLongitude(-0.376312345);
        p.setAccuracy(5); p.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos()); return p;
    }
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
