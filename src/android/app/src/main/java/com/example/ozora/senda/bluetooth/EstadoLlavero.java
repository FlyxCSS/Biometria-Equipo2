/*
 * Nombre: EstadoLlavero.java
 * Descripción: Aplicación de campo: acceso, captura, recorridos y presentación Android.
 * Copyright: (c) 2026 Equipo 2 - SENDA / OZORA.
 * Fecha: 19/11/2026
 * Autores: Fabián Andrés Useche Rivera; Élia Garcia Pons; Judith Civico Aporta; Julia Valen de Oliveira; Soufiane Belfquih El Khalfi
 * Aportación: Aplicación de campo: acceso, captura, recorridos y presentación Android.
 */
package com.example.ozora.senda.bluetooth;

/** Ultimas lecturas BLE en memoria, por llavero y con caducidad. No inventa mediciones. */
public final class EstadoLlavero {
    public static final EstadoLlavero COMPARTIDO = new EstadoLlavero();
    public static final long VIGENCIA_MS = 30_000L;
    private String sensor;
    private Double temperatura;
    private Integer bateria;
    private long instanteTemperatura = -1, instanteBateria = -1;

    /** --------------------
     * Diseño lógico: reiniciar() -->
     * Descripción: Reiniciar. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    public synchronized void reiniciar() {
        sensor = null; temperatura = null; bateria = null;
        instanteTemperatura = instanteBateria = -1;
    }

    /** --------------------
     * Diseño lógico: trama: IBeaconParserResultado, ahora: Z --> actualizar() -->
     * Descripción: Actualizar. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    public synchronized void actualizar(IBeaconParser.Resultado trama, long ahora) {
        if (trama == null || trama.nombreBle == null || ahora < 0) return;
        if (!trama.nombreBle.equalsIgnoreCase(sensor)) { reiniciar(); sensor = trama.nombreBle; }
        if (trama.esTemperatura()) { temperatura = trama.obtenerTemperaturaC(); instanteTemperatura = ahora; }
        if (trama.esBateria()) { bateria = trama.obtenerBateriaPorcentaje(); instanteBateria = ahora; }
    }

    /** --------------------
     * Diseño lógico: seleccionado: Text, instante: Z, ahora: Z --> vigente() -->
     * B <--
     * Descripción: Vigente. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    private boolean vigente(String seleccionado, long instante, long ahora) {
        return seleccionado != null && seleccionado.equalsIgnoreCase(sensor)
                && instante >= 0 && ahora >= instante && ahora - instante <= VIGENCIA_MS;
    }

    /** --------------------
     * Diseño lógico: seleccionado: Text, ahora: Z --> temperatura() -->
     * R <--
     * Descripción: Temperatura. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    public synchronized Double temperatura(String seleccionado, long ahora) {
        return vigente(seleccionado, instanteTemperatura, ahora) ? temperatura : null;
    }

    /** --------------------
     * Diseño lógico: seleccionado: Text, ahora: Z --> bateria() -->
     * Z <--
     * Descripción: Bateria. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    public synchronized Integer bateria(String seleccionado, long ahora) {
        return vigente(seleccionado, instanteBateria, ahora) ? bateria : null;
    }
}
