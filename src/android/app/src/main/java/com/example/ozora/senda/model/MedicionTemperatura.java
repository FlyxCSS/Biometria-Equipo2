package com.example.ozora.senda.model;

/** Temperatura BLE en centésimas con la ubicación validada de su recepción. */
public final class MedicionTemperatura {
    public final String idLlavero;
    public final int centesimas;
    public final String fechaIso8601;
    public final double latitud, longitud;

    public MedicionTemperatura(String idLlavero, int centesimas, String fechaIso8601,
                              double latitud, double longitud) {
        this.idLlavero = idLlavero;
        this.centesimas = centesimas;
        this.fechaIso8601 = fechaIso8601;
        this.latitud = latitud;
        this.longitud = longitud;
    }
}
