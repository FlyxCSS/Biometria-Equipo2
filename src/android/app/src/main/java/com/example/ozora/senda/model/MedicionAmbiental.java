package com.example.ozora.senda.model;

/** Nombre utilizado por el controlador BLE de Persona 1. */
public final class MedicionAmbiental extends Measurement {
    public MedicionAmbiental(String id, int ppb, double lat, double lon, float precision, String fecha, int contador) {
        super(id, ppb, lat, lon, precision, fecha, contador);
    }
}
