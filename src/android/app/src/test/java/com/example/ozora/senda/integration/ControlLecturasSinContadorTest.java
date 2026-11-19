/*
 * Nombre: ControlLecturasSinContadorTest.java
 * Descripción: Aplicación de campo: acceso, captura, recorridos y presentación Android.
 * Copyright: (c) 2026 Equipo 2 - SENDA / OZORA.
 * Fecha: 19/11/2026
 * Autores: Fabián Andrés Useche Rivera; Élia Garcia Pons; Judith Civico Aporta; Julia Valen de Oliveira; Soufiane Belfquih El Khalfi
 * Aportación: Aplicación de campo: acceso, captura, recorridos y presentación Android.
 */
package com.example.ozora.senda.integration;
import org.junit.Test;
import static org.junit.Assert.*;
public class ControlLecturasSinContadorTest {

    /** --------------------
     * Diseño lógico: descartaRafagaPeroAceptaLaSiguienteAunqueElValorSeaIgual() -->
     * Descripción: Descarta Rafaga Pero Acepta La Siguiente Aunque El Valor Sea Igual. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    @Test public void descartaRafagaPeroAceptaLaSiguienteAunqueElValorSeaIgual() {
        ControlLecturasSinContador c=new ControlLecturasSinContador();
        assertEquals(0,c.aceptar("S-001",0));
        assertEquals(-1,c.aceptar("S-001",100));
        assertEquals(-1,c.aceptar("S-001",2999));
        assertEquals(1,c.aceptar("S-001",9000));
        c.reiniciar(); assertEquals(0,c.aceptar("S-001",10000));
    }

    /** --------------------
     * Diseño lógico: contadorLocalValidoParaContrato() -->
     * Descripción: Contador Local Valido Para Contrato. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    @Test public void contadorLocalValidoParaContrato() {
        ControlLecturasSinContador c=new ControlLecturasSinContador();
        for(int i=0;i<260;i++) assertEquals(i&255,c.aceptar("S-001",i*9000L));
        assertEquals(-1,c.aceptar(null,3000000));
    }
}
