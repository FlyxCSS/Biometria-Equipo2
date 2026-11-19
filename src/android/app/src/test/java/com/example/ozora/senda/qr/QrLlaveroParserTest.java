/*
 * Nombre: QrLlaveroParserTest.java
 * Descripción: Aplicación de campo: acceso, captura, recorridos y presentación Android.
 * Copyright: (c) 2026 Equipo 2 - SENDA / OZORA.
 * Fecha: 02/11/2026
 * Autores: Fabián Andrés Useche Rivera; Élia Garcia Pons; Judith Civico Aporta; Julia Valen de Oliveira; Soufiane Belfquih El Khalfi
 * Aportación: Aplicación de campo: acceso, captura, recorridos y presentación Android.
 */
package com.example.ozora.senda.qr;
import org.junit.Test;
import static org.junit.Assert.*;
public class QrLlaveroParserTest {


    /** --------------------
     * Diseño lógico: permiteCodigosSenda() -->
     * Descripción: Permite Codigos Senda. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    @Test public void permiteCodigosSenda() {
        assertEquals("S-001", QrLlaveroParser.extraerIdentificador(" s-001 "));
        assertEquals("S-123456", QrLlaveroParser.extraerIdentificador("S-123456"));
    }


    /** --------------------
     * Diseño lógico: rechazaOtrosCodigos() -->
     * Descripción: Rechaza Otros Codigos. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    @Test public void rechazaOtrosCodigos() {
        assertNull(QrLlaveroParser.extraerIdentificador("S-ABC"));
        assertNull(QrLlaveroParser.extraerIdentificador("https://example.com"));
        assertNull(QrLlaveroParser.extraerIdentificador(""));
        assertNull(QrLlaveroParser.extraerIdentificador(null));
    }
}
