/*
 * Nombre: RecoveryTokenTest.java
 * Descripción: Aplicación de campo: acceso, captura, recorridos y presentación Android.
 * Copyright: (c) 2026 Equipo 2 - SENDA / OZORA.
 * Fecha: 19/11/2026
 * Autores: Fabián Andrés Useche Rivera; Élia Garcia Pons; Judith Civico Aporta; Julia Valen de Oliveira; Soufiane Belfquih El Khalfi
 * Aportación: Aplicación de campo: acceso, captura, recorridos y presentación Android.
 */
package com.example.ozora.senda.auth;
import org.junit.Test;
import static org.junit.Assert.*;
public class RecoveryTokenTest {
    private final String token = "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef";

    /** --------------------
     * Diseño lógico: aceptaTokenCompletoYEnlaceDelServidor() -->
     * Descripción: Acepta Token Completo YEnlace Del Servidor. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    @Test public void aceptaTokenCompletoYEnlaceDelServidor() {
        assertEquals(token, RecoveryToken.extract("  " + token + " "));
        assertEquals(token, RecoveryToken.extract("https://parque.example/senda/acceso#token=" + token));
    }

    /** --------------------
     * Diseño lógico: rechazaCodigosCortosYFormatosAjenos() -->
     * Descripción: Rechaza Codigos Cortos YFormatos Ajenos. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    @Test public void rechazaCodigosCortosYFormatosAjenos() {
        for (String value : new String[]{null, "", "1234", token.substring(1), token + "a",
                token.toUpperCase(), "javascript:alert(1)", "https://parque.example/?token=" + token,
                "http://parque.example/acceso#token=" + token})
            assertNull(RecoveryToken.extract(value));
    }

    /** --------------------
     * Diseño lógico: rechazaTokensAmbiguosYFragmentosMalformados() -->
     * Descripción: Rechaza Tokens Ambiguos YFragmentos Malformados. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    @Test public void rechazaTokensAmbiguosYFragmentosMalformados() {
        assertNull(RecoveryToken.extract("https://parque.example/acceso#token=" + token + "&token=" + token));
        assertNull(RecoveryToken.extract("https://parque.example/acceso#token=%ZZ"));
        assertNull(RecoveryToken.extract("https://parque.example/acceso#otro=" + token));
    }

    /** --------------------
     * Diseño lógico: extraerNoDependeDeContactarConElHost() -->
     * Descripción: Extraer No Depende De Contactar Con El Host. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    @Test public void extraerNoDependeDeContactarConElHost() {
        assertEquals(token, RecoveryToken.extract("https://sin-red.invalid/acceso#token=" + token));
    }
}
