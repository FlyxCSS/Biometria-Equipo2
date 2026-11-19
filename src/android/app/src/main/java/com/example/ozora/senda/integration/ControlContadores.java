/*
 * Nombre: ControlContadores.java
 * Descripción: Filtra contadores repetidos en una ventana temporal.
 * Copyright: (c) 2026 Equipo 2 - SENDA / OZORA.
 * Fecha: 02/11/2026
 * Autores: Fabián Andrés Useche Rivera; Élia Garcia Pons; Judith Civico Aporta; Julia Valen de Oliveira; Soufiane Belfquih El Khalfi
 * Aportación: Filtra contadores repetidos en una ventana temporal.
 */
package com.example.ozora.senda.integration;

/** Filtro de duplicados de ventana corta. NO garantiza unicidad absoluta ni autenticidad. */
public final class ControlContadores {
    public static final long VENTANA_MS = 4000L;
    private String ultimoLlavero;
    private int ultimoContador = -1;
    private long ultimoInstante = -1L;



    /** --------------------
     * Diseño lógico: llavero: Text, contador: Z, ahoraMs: Z --> aceptar() -->
     * B <--
     * Descripción: Descarta el mismo contador solo si aparece inmediatamente otra vez
     * -------------------- */
    public boolean aceptar(String llavero, int contador, long ahoraMs) {
        if (llavero == null || contador < 0 || contador > 255 || ahoraMs < 0) return false;
        if (llavero.equals(ultimoLlavero) && contador == ultimoContador
                && ultimoInstante >= 0 && ahoraMs >= ultimoInstante
                && ahoraMs - ultimoInstante < VENTANA_MS) return false;
        ultimoLlavero = llavero;
        ultimoContador = contador;
        ultimoInstante = ahoraMs;
        return true;
    }



    /** --------------------
     * Diseño lógico: reiniciar() -->
     * Descripción: Reiniciar. Filtra contadores repetidos en una ventana temporal
     * -------------------- */
    public void reiniciar() { ultimoLlavero = null; ultimoContador = -1; ultimoInstante = -1L; }
}
