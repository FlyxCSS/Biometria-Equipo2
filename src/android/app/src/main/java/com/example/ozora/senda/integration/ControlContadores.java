/*
 * Nombre: ControlContadores.java
 * Descripción: Deduplicación local de anuncios BLE por contador y tiempo.
 * Copyright (c) 2026 - Equipo 2 - SENDA / OZORA
 * Fecha: 02/11/2026
 * Autor: ...
 * Aportación: Acepta reinicios y vuelta de 255 a 0 sin confiar en el contador como ID global.
 */
package com.example.ozora.senda.integration;

/** Filtro de duplicados de ventana corta. NO garantiza unicidad absoluta ni autenticidad. */
public final class ControlContadores {
    public static final long VENTANA_MS = 4000L;
    private String ultimoLlavero;
    private int ultimoContador = -1;
    private long ultimoInstante = -1L;

    /** Diseño lógico: (String, int, long) -> aceptar() -> (boolean).
     * Descripción: Descarta el mismo contador solo si aparece inmediatamente otra vez. */
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

    /** Diseño lógico: () -> reiniciar() -> (void). */
    public void reiniciar() { ultimoLlavero = null; ultimoContador = -1; ultimoInstante = -1L; }
}
