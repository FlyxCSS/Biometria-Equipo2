package com.example.ozora.senda.bluetooth;
import org.junit.Test;
import static org.junit.Assert.*;
public class TelemetriaTest {
    private IBeaconParser.Resultado trama(int tipo, int valor, String nombre) {
        byte[] b = new byte[23]; b[0] = 2; b[1] = 0x15;
        String uuid = IBeaconParser.UUID_PROYECTO.replace("-", "");
        for (int i=0; i<16; i++) b[i+2]=(byte)Integer.parseInt(uuid.substring(i*2,i*2+2),16);
        b[18]=(byte)(tipo>>8); b[19]=(byte)tipo;
        b[20]=(byte)(valor>>8); b[21]=(byte)valor; b[22]=-53;
        return IBeaconParser.interpretar(b,nombre,-70);
    }
    @Test public void majorDirectoYUnidades() {
        IBeaconParser.Resultado o3=trama(11,347,"S-001");
        assertEquals(11,o3.major); assertEquals(-1,o3.contador);
        assertEquals(0.347,o3.obtenerOzonoPpm(),0.000001);
        assertEquals(23.45,trama(12,2345,"S-001").obtenerTemperaturaC(),0.000001);
        assertEquals(-5.25,trama(12,(-525)&65535,"S-001").obtenerTemperaturaC(),0.000001);
        assertEquals(Integer.valueOf(72),trama(14,72,"S-001").obtenerBateriaPorcentaje());
        assertFalse(trama(14,72,"S-001").esOzono());
    }
    @Test public void recibeLaSecuenciaRealDelMicroConContador() {
        EstadoLlavero estado = new EstadoLlavero();
        IBeaconParser.Resultado primera = trama(3116,2941,"S-001");
        assertTrue(primera.esTemperatura());
        assertEquals(12,primera.tipo);
        assertEquals(44,primera.contador);
        assertEquals(29.41,primera.obtenerTemperaturaC(),0.000001);
        estado.actualizar(primera,1000);
        estado.actualizar(trama(3628,65535,"S-001"),4000);
        IBeaconParser.Resultado ozono = trama(2861,0,"S-001");
        assertTrue(ozono.esOzono());
        assertEquals(45,ozono.contador);
        assertEquals(Integer.valueOf(0),ozono.obtenerOzonoPpb());
        estado.actualizar(ozono,7000);
        // Las otras magnitudes no borran la temperatura de la tanda anterior.
        assertEquals(29.41,estado.temperatura("S-001",7000),0.000001);
        IBeaconParser.Resultado segunda = trama(3117,2939,"S-001");
        assertEquals(45,segunda.contador);
        assertEquals(29.39,segunda.obtenerTemperaturaC(),0.000001);
        estado.actualizar(segunda,10000);
        estado.actualizar(trama(3629,65535,"S-001"),13000);
        assertEquals(29.39,estado.temperatura("S-001",13000),0.000001);
        assertNull(estado.bateria("S-001",13000));
    }
    @Test public void valoresDesconocidosYLimites() {
        assertNull(trama(11,65535,"S-001").obtenerOzonoPpb());
        assertNull(trama(12,0x8000,"S-001").obtenerTemperaturaC());
        assertNull(trama(12,5001,"S-001").obtenerTemperaturaC());
        assertNull(trama(14,65535,"S-001").obtenerBateriaPorcentaje());
        assertNull(trama(14,101,"S-001").obtenerBateriaPorcentaje());
        assertEquals(Integer.valueOf(0),trama(14,0,"S-001").obtenerBateriaPorcentaje());
        assertEquals(Integer.valueOf(100),trama(14,100,"S-001").obtenerBateriaPorcentaje());
        assertNull(trama(15,12,"S-001"));
    }
    @Test public void caducaCadaLecturaYSeparaLlaveros() {
        EstadoLlavero e=new EstadoLlavero();
        e.actualizar(trama(12,2345,"S-001"),1000);
        e.actualizar(trama(14,72,"S-001"),2000);
        assertEquals(23.45,e.temperatura("s-001",3000),0.000001);
        assertEquals(Integer.valueOf(72),e.bateria("S-001",3000));
        assertNull(e.bateria("S-002",3000));
        assertNull(e.bateria("S-001",0));
        assertNull(e.temperatura("S-001",31001));
        assertNotNull(e.bateria("S-001",31001));
        e.actualizar(trama(14,65535,"S-001"),32000);
        assertNull(e.bateria("S-001",32001));
        e.actualizar(trama(14,80,"S-002"),33000);
        assertNull(e.temperatura("S-002",33001));
        assertNull(e.bateria("S-001",33001));
        e.reiniciar(); assertNull(e.bateria("S-002",33001));
    }
}
