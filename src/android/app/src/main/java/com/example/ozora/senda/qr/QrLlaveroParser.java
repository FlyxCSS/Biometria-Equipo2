/*
 * Nombre: QrLlaveroParser.java
 * Descripción: Aplicación de campo: acceso, captura, recorridos y presentación Android.
 * Copyright: (c) 2026 Equipo 2 - SENDA / OZORA.
 * Fecha: 02/11/2026
 * Autores: Fabián Andrés Useche Rivera; Élia Garcia Pons; Judith Civico Aporta; Julia Valen de Oliveira; Soufiane Belfquih El Khalfi
 * Aportación: Aplicación de campo: acceso, captura, recorridos y presentación Android.
 */
package com.example.ozora.senda.qr;

import java.util.Locale;
import java.util.regex.Pattern;

/** Valida la referencia de llavero contenida en un código QR. */
public final class QrLlaveroParser {

    private static final Pattern FORMATO_ID = Pattern.compile("S-[0-9]{3,6}");



    /** --------------------
     * Diseño lógico: QrLlaveroParser() -->
     * QrLlaveroParser <--
     * Descripción: Impide crear instancias de una clase con métodos estáticos
     * -------------------- */
    private QrLlaveroParser() {
        // Clase de utilidades.
    }



    /** --------------------
     * Diseño lógico: textoQr: Text --> extraerIdentificador() --x
     * Text <--
     * Descripción: Normaliza y valida un identificador SENDA del tipo S-001
     * -------------------- */
    public static String extraerIdentificador(String textoQr) {
        if (textoQr == null) {
            return null;
        }
        String identificador = textoQr.trim().toUpperCase(Locale.ROOT);
        return FORMATO_ID.matcher(identificador).matches() ? identificador : null;
    }
}
