/* Nombre: QrLlaveroParserTest.java | Descripción: Test del formato QR | Fecha: 02/11/2026 | Autor: ... */
package com.example.ozora.senda.qr;
import org.junit.Test;
import static org.junit.Assert.*;
public class QrLlaveroParserTest {
    /** Diseño lógico: () -> permiteCodigosSenda() -> (void). */
    @Test public void permiteCodigosSenda() {
        assertEquals("S-001", QrLlaveroParser.extraerIdentificador(" s-001 "));
        assertEquals("S-123456", QrLlaveroParser.extraerIdentificador("S-123456"));
    }
    /** Diseño lógico: () -> rechazaOtrosCodigos() -> (void). */
    @Test public void rechazaOtrosCodigos() {
        assertNull(QrLlaveroParser.extraerIdentificador("S-ABC"));
        assertNull(QrLlaveroParser.extraerIdentificador("https://example.com"));
        assertNull(QrLlaveroParser.extraerIdentificador(""));
        assertNull(QrLlaveroParser.extraerIdentificador(null));
    }
}
