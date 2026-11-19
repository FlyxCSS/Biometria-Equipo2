package com.example.ozora.senda.bluetooth;

/** Ultimas lecturas BLE en memoria, por llavero y con caducidad. No inventa mediciones. */
public final class EstadoLlavero {
    public static final EstadoLlavero COMPARTIDO = new EstadoLlavero();
    public static final long VIGENCIA_MS = 30_000L;
    private String sensor;
    private Double temperatura;
    private Integer bateria;
    private long instanteTemperatura = -1, instanteBateria = -1;
    public synchronized void reiniciar() {
        sensor = null; temperatura = null; bateria = null;
        instanteTemperatura = instanteBateria = -1;
    }
    public synchronized void actualizar(IBeaconParser.Resultado trama, long ahora) {
        if (trama == null || trama.nombreBle == null || ahora < 0) return;
        if (!trama.nombreBle.equalsIgnoreCase(sensor)) { reiniciar(); sensor = trama.nombreBle; }
        if (trama.esTemperatura()) { temperatura = trama.obtenerTemperaturaC(); instanteTemperatura = ahora; }
        if (trama.esBateria()) { bateria = trama.obtenerBateriaPorcentaje(); instanteBateria = ahora; }
    }
    private boolean vigente(String seleccionado, long instante, long ahora) {
        return seleccionado != null && seleccionado.equalsIgnoreCase(sensor)
                && instante >= 0 && ahora >= instante && ahora - instante <= VIGENCIA_MS;
    }
    public synchronized Double temperatura(String seleccionado, long ahora) {
        return vigente(seleccionado, instanteTemperatura, ahora) ? temperatura : null;
    }
    public synchronized Integer bateria(String seleccionado, long ahora) {
        return vigente(seleccionado, instanteBateria, ahora) ? bateria : null;
    }
}
