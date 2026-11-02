/*
 * Nombre: QrLlaveroParser.java
 * Descripción: Valida el identificador de llavero incluido en un QR de SENDA.
 * Copyright (c) 2026 - Equipo 2 - SENDA / OZORA
 * Fecha: 02/11/2026
 * Autor: ...
 * Aportación: Interpretación local del contenido QR, sin asignar llaveros ni acceder al servidor.
 */
package com.example.ozora.senda.qr;

import java.util.Locale;
import java.util.regex.Pattern;

/** Valida la referencia de llavero contenida en un código QR. */
public final class QrLlaveroParser {

    private static final Pattern FORMATO_ID = Pattern.compile("S-[0-9]{3,6}");

    /*
     * Diseño lógico: () -> QrLlaveroParser() -> (constructor privado)
     * Descripción: Impide crear instancias de una clase con métodos estáticos.
     */
    private QrLlaveroParser() {
        // Clase de utilidades.
    }

    /*
     * Diseño lógico: (String textoQr) -> extraerIdentificador(String) -> (String)
     * Descripción: Normaliza y valida un identificador SENDA del tipo S-001.
     *              Devuelve null si el contenido QR no es válido.
     *              La validación local no sustituye la autorización del servidor.
     */
    public static String extraerIdentificador(String textoQr) {
        if (textoQr == null) {
            return null;
        }
        String identificador = textoQr.trim().toUpperCase(Locale.ROOT);
        return FORMATO_ID.matcher(identificador).matches() ? identificador : null;
    }
}
