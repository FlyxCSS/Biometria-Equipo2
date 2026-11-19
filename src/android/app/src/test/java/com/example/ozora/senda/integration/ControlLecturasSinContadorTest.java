package com.example.ozora.senda.integration;
import org.junit.Test;
import static org.junit.Assert.*;
public class ControlLecturasSinContadorTest {
    @Test public void descartaRafagaPeroAceptaLaSiguienteAunqueElValorSeaIgual() {
        ControlLecturasSinContador c=new ControlLecturasSinContador();
        assertEquals(0,c.aceptar("S-001",0));
        assertEquals(-1,c.aceptar("S-001",100));
        assertEquals(-1,c.aceptar("S-001",2999));
        assertEquals(1,c.aceptar("S-001",9000));
        c.reiniciar(); assertEquals(0,c.aceptar("S-001",10000));
    }
    @Test public void contadorLocalValidoParaContrato() {
        ControlLecturasSinContador c=new ControlLecturasSinContador();
        for(int i=0;i<260;i++) assertEquals(i&255,c.aceptar("S-001",i*9000L));
        assertEquals(-1,c.aceptar(null,3000000));
    }
}
