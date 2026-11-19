/*
 * Nombre: EnvironmentalAssessment.java
 * Descripción: Aplicación de campo: acceso, captura, recorridos y presentación Android.
 * Copyright: (c) 2026 Equipo 2 - SENDA / OZORA.
 * Fecha: 19/11/2026
 * Autores: Fabián Andrés Useche Rivera; Élia Garcia Pons; Judith Civico Aporta; Julia Valen de Oliveira; Soufiane Belfquih El Khalfi
 * Aportación: Aplicación de campo: acceso, captura, recorridos y presentación Android.
 */
package com.example.ozora.senda.statistics;

public final class EnvironmentalAssessment {

    /** --------------------
     * Diseño lógico: ppb: Z, threshold: Z --> aviso() --x
     * Text <--
     * Descripción: Aviso. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    public static String aviso(int ppb, int threshold) {
        if (ppb < threshold) return "";
        return "Aviso orientativo · La última lectura alcanza el umbral configurado de " + threshold + " ppb.";
    }
}
