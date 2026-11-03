/*
 * Nombre: CapturaLlaveroController.java
 * Descripción: API reutilizable de recepción BLE y ubicación sin depender de pantallas.
 * Copyright (c) 2026 - Equipo 2 - SENDA / OZORA
 * Fecha: 02/11/2026
 * Autor: ...
 * Aportación: Contrato estable para las pantallas de Persona 2 y red de Persona 3.
 */
package com.example.ozora.senda.integration;

import android.content.Context;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import com.example.ozora.senda.bluetooth.BleScanner;
import com.example.ozora.senda.bluetooth.IBeaconParser;
import com.example.ozora.senda.location.LocationTracker;
import com.example.ozora.senda.model.MedicionAmbiental;
import com.example.ozora.senda.qr.QrLlaveroParser;

/** La selección QR NO es autorización. No transmite al servidor. */
public final class CapturaLlaveroController implements BleScanner.Oyente, LocationTracker.Oyente {
    public interface Oyente {
        default void alRecibirAnuncio() {}
        /** Diseño lógico: (String) -> alCambiarEstado() -> (void). */
        void alCambiarEstado(String mensaje);
        /** Diseño lógico: (Location) -> alRecibirUbicacion() -> (void). */
        void alRecibirUbicacion(Location ubicacion);
        /** Diseño lógico: (MedicionAmbiental, int) -> alPrepararMedicion() -> (void). */
        void alPrepararMedicion(MedicionAmbiental medicion, int rssi);
        /** Diseño lógico: (String) -> alDescartarMedicion() -> (void). */
        void alDescartarMedicion(String motivo);
    }
    private final BleScanner ble;
    private final LocationTracker gps;
    private final Oyente oyente;
    private final Handler principal = new Handler(Looper.getMainLooper());
    private final PreparadorMediciones preparador = new PreparadorMediciones();
    private String llavero;
    private Location ultima;
    private boolean activo;
    private IBeaconParser.Resultado pendienteUbicacion;
    private long instantePendienteMs;
    private static final long MAX_ESPERA_TRAMA_MS = 10_000L;

    /** Diseño lógico: (Context, Oyente) -> CapturaLlaveroController() -> (CapturaLlaveroController). */
    public CapturaLlaveroController(Context contexto, Oyente oyente) {
        if (oyente == null) throw new IllegalArgumentException("Oyente obligatorio");
        this.oyente = oyente;
        this.ble = new BleScanner(contexto, this);
        this.gps = new LocationTracker(contexto, this);
    }
    /** Diseño lógico: (String) -> seleccionarQr() -> (boolean).
     * Descripción: Selecciona localmente; no consulta inventario ni asigna trabajador. */
    public boolean seleccionarQr(String texto) {
        String normalizado = QrLlaveroParser.extraerIdentificador(texto);
        if (normalizado == null) return false;
        detener(); llavero = normalizado; ultima = null; preparador.reiniciar();
        informar("QR aceptado localmente: " + llavero + ". Pendiente de autorización REST.");
        return true;
    }
    /** Diseño lógico: () -> getLlaveroSeleccionado() -> (String). */
    public String getLlaveroSeleccionado() { return llavero; }
    /** Diseño lógico: () -> iniciar() -> (boolean).
     * Descripción: Requiere permisos concedidos previamente por Activity. */
    public boolean iniciar() {
        if (llavero == null) { informar("Escanea primero un QR válido."); return false; }
        detener(); ultima = null; preparador.reiniciar(); activo = true;
        boolean okGps = gps.iniciar();
        boolean okBle = ble.iniciarEscaneo();
        if (!okGps || !okBle) {
            detener();
            informar("No se pudo iniciar BLE y ubicación. Revisa permisos, Bluetooth y ubicación.");
            return false;
        }
        informar("Capturando " + llavero + ". Buscando ubicación reciente...");
        return true;
    }
    /** Diseño lógico: () -> detener() -> (void). */
    public void detener() { activo = false; principal.removeCallbacksAndMessages(null); ble.detenerEscaneo(); gps.detener(); ultima = null; pendienteUbicacion = null; instantePendienteMs = 0; }
    /** Diseño lógico: (IBeaconParser.Resultado, String) -> alRecibirTrama() -> (void). */
    @Override public void alRecibirTrama(IBeaconParser.Resultado trama, String mac) {
        principal.post(() -> {
            if (!activo || llavero == null || trama == null || trama.nombreBle == null
                    || !llavero.equalsIgnoreCase(trama.nombreBle) || !trama.esOzono()) return;
            oyente.alRecibirAnuncio();
            // Se recuerda la trama mientras llega la primera ubicación; no se considera enviada.
            if (ultima == null) {
                pendienteUbicacion = trama;
                instantePendienteMs = SystemClock.elapsedRealtime();
                return;
            }
            publicarSiValida(trama);
        });
    }
    /** Diseño lógico: (Location) -> alRecibirUbicacion() -> (void). */
    @Override public void alRecibirUbicacion(Location ubicacion) {
        principal.post(() -> {
            if (!activo) return;
            ultima = ubicacion;
            oyente.alRecibirUbicacion(ubicacion);
            if (pendienteUbicacion != null) {
                IBeaconParser.Resultado trama = pendienteUbicacion;
                long edad = SystemClock.elapsedRealtime() - instantePendienteMs;
                pendienteUbicacion = null;
                if (edad >= 0 && edad <= MAX_ESPERA_TRAMA_MS) publicarSiValida(trama);
            }
        });
    }
    /** Diseño lógico: (IBeaconParser.Resultado) -> publicarSiValida() -> (void).
     * Descripción: Prepara una lectura solo cuando haya posición válida; evita reenviar repetidas. */
    private void publicarSiValida(IBeaconParser.Resultado trama) {
        PreparadorMediciones.Resultado r = preparador.preparar(llavero, trama, ultima);
        if (r.medicion != null) oyente.alPrepararMedicion(r.medicion, trama.rssi);
        else if (r.mensaje != null && !r.mensaje.startsWith("Anuncio BLE repetido"))
            oyente.alDescartarMedicion(r.mensaje);
    }
    /** Diseño lógico: (String) -> alOcurrirError() -> (void). */
    @Override public void alOcurrirError(String mensaje) { informar(mensaje); }
    /** Diseño lógico: (String) -> informar() -> (void). */
    private void informar(String mensaje) { principal.post(() -> oyente.alCambiarEstado(mensaje)); }
}
