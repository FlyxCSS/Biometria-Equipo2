/*
 * Nombre: ControlLecturasSinContador.java
 * Descripción: Limita duplicados cuando la trama no aporta contador.
 * Copyright: (c) 2026 Equipo 2 - SENDA / OZORA.
 * Fecha: 19/11/2026
 * Autores: Fabián Andrés Useche Rivera; Élia Garcia Pons; Judith Civico Aporta; Julia Valen de Oliveira; Soufiane Belfquih El Khalfi
 * Aportación: Limita duplicados cuando la trama no aporta contador.
 */
package com.example.ozora.senda.integration;

/** Limita repeticiones cuando Major solo contiene el tipo: no existe un ID de muestra BLE. */
public final class ControlLecturasSinContador {
    private String sensor;
    private long instante = -1;
    private int secuencia;

    /** --------------------
     * Diseño lógico: seleccionado: Text, ahora: Z --> aceptar() -->
     * Z <--
     * Descripción: Aceptar. Limita duplicados cuando la trama no aporta contador
     * -------------------- */
    public int aceptar(String seleccionado, long ahora) {
        if (seleccionado == null || ahora < 0) return -1;
        if (seleccionado.equals(sensor) && instante >= 0 && ahora >= instante
                && ahora - instante < ControlContadores.VENTANA_MS) return -1;
        sensor = seleccionado;
        instante = ahora;
        int resultado = secuencia;
        secuencia = (secuencia + 1) & 255;
        return resultado;
    }

    /** --------------------
     * Diseño lógico: reiniciar() -->
     * Descripción: Reiniciar. Limita duplicados cuando la trama no aporta contador
     * -------------------- */
    public void reiniciar() { sensor = null; instante = -1; secuencia = 0; }
}
