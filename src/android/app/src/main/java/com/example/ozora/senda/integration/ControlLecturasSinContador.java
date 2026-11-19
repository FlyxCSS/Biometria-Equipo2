package com.example.ozora.senda.integration;

/** Limita repeticiones cuando Major solo contiene el tipo: no existe un ID de muestra BLE. */
public final class ControlLecturasSinContador {
    private String sensor;
    private long instante = -1;
    private int secuencia;
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
    public void reiniciar() { sensor = null; instante = -1; secuencia = 0; }
}
