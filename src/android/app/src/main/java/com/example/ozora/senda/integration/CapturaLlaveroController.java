/*
 * Nombre: CapturaLlaveroController.java
 * Descripción: Relaciona anuncios del llavero con ubicación y preparadores.
 * Copyright: (c) 2026 Equipo 2 - SENDA / OZORA.
 * Fecha: 02/11/2026
 * Autores: Fabián Andrés Useche Rivera; Élia Garcia Pons; Judith Civico Aporta; Julia Valen de Oliveira; Soufiane Belfquih El Khalfi
 * Aportación: Relaciona anuncios del llavero con ubicación y preparadores.
 */
package com.example.ozora.senda.integration;

import android.content.Context;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import com.example.ozora.senda.bluetooth.BleScanner;
import com.example.ozora.senda.bluetooth.IBeaconParser;
import com.example.ozora.senda.bluetooth.EstadoLlavero;
import com.example.ozora.senda.location.LocationTracker;
import com.example.ozora.senda.model.MedicionAmbiental;
import com.example.ozora.senda.model.MedicionTemperatura;
import com.example.ozora.senda.qr.QrLlaveroParser;

/** La selección QR NO es autorización. No transmite al servidor. */
public final class CapturaLlaveroController implements BleScanner.Oyente, LocationTracker.Oyente {
    public interface Oyente {

        /** --------------------
         * Diseño lógico: alRecibirAnuncio() -->
         * Descripción: Al Recibir Anuncio. Aplicación de campo: acceso, captura, recorridos y presentación Android
         * -------------------- */
        default void alRecibirAnuncio() {}

        /** --------------------
         * Diseño lógico: medicion: MedicionTemperatura --> alPrepararTemperatura() -->
         * Descripción: Al Preparar Temperatura. Aplicación de campo: acceso, captura, recorridos y presentación Android
         * -------------------- */
        default void alPrepararTemperatura(MedicionTemperatura medicion) {}


        /** --------------------
         * Diseño lógico: mensaje: Text --> alCambiarEstado() -->
         * Descripción: Al Cambiar Estado. Aplicación de campo: acceso, captura, recorridos y presentación Android
         * -------------------- */
        void alCambiarEstado(String mensaje);


        /** --------------------
         * Diseño lógico: ubicacion: Posicion --> alRecibirUbicacion() -->
         * Descripción: Al Recibir Ubicacion. Aplicación de campo: acceso, captura, recorridos y presentación Android
         * -------------------- */
        void alRecibirUbicacion(Location ubicacion);


        /** --------------------
         * Diseño lógico: medicion: MedicionAmbiental, rssi: Z --> alPrepararMedicion() -->
         * Descripción: Al Preparar Medicion. Aplicación de campo: acceso, captura, recorridos y presentación Android
         * -------------------- */
        void alPrepararMedicion(MedicionAmbiental medicion, int rssi);


        /** --------------------
         * Diseño lógico: motivo: Text --> alDescartarMedicion() -->
         * Descripción: Al Descartar Medicion. Aplicación de campo: acceso, captura, recorridos y presentación Android
         * -------------------- */
        void alDescartarMedicion(String motivo);
    }
    private final BleScanner ble;
    private final LocationTracker gps;
    private final Oyente oyente;
    private final Handler principal = new Handler(Looper.getMainLooper());
    private final PreparadorMediciones preparador = new PreparadorMediciones();
    private String llavero;
    private final PreparadorTemperaturas preparadorTemperaturas = new PreparadorTemperaturas();
    private IBeaconParser.Resultado temperaturaPendiente;
    private long instanteTemperaturaPendiente;
    private Location ultima;
    private boolean activo;
    private IBeaconParser.Resultado pendienteUbicacion;
    private long instantePendienteMs;
    private static final long MAX_ESPERA_TRAMA_MS = 10_000L;



    /** --------------------
     * Diseño lógico: contexto: Entorno, oyente: Oyente --> CapturaLlaveroController() -->
     * CapturaLlaveroController <--
     * Descripción: Construye CapturaLlaveroController. Relaciona anuncios del llavero con ubicación y preparadores
     * -------------------- */
    public CapturaLlaveroController(Context contexto, Oyente oyente) {
        if (oyente == null) throw new IllegalArgumentException("Oyente obligatorio");
        this.oyente = oyente;
        this.ble = new BleScanner(contexto, this);
        this.gps = new LocationTracker(contexto, this);
    }


    /** --------------------
     * Diseño lógico: texto: Text --> seleccionarQr() -->
     * B <--
     * Descripción: Selecciona localmente; no consulta inventario ni asigna trabajador
     * -------------------- */
    public boolean seleccionarQr(String texto) {
        String normalizado = QrLlaveroParser.extraerIdentificador(texto);
        if (normalizado == null) return false;
        detener(); llavero = normalizado; ultima = null; preparador.reiniciar();
        informar("QR aceptado localmente: " + llavero + ". Pendiente de autorización REST.");
        return true;
    }


    /** --------------------
     * Diseño lógico: getLlaveroSeleccionado() -->
     * Text <--
     * Descripción: Get Llavero Seleccionado. Relaciona anuncios del llavero con ubicación y preparadores
     * -------------------- */
    public String getLlaveroSeleccionado() { return llavero; }


    /** --------------------
     * Diseño lógico: iniciar() -->
     * B <--
     * Descripción: Requiere permisos concedidos previamente por Activity
     * -------------------- */
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


    /** --------------------
     * Diseño lógico: detener() -->
     * Descripción: Detener. Relaciona anuncios del llavero con ubicación y preparadores
     * -------------------- */
    public void detener() { preparadorTemperaturas.reiniciar(); temperaturaPendiente = null; instanteTemperaturaPendiente = 0; activo = false; EstadoLlavero.COMPARTIDO.reiniciar(); principal.removeCallbacksAndMessages(null); ble.detenerEscaneo(); gps.detener(); ultima = null; pendienteUbicacion = null; instantePendienteMs = 0; }


    /** --------------------
     * Diseño lógico: trama: IBeaconParserResultado, mac: Text --> alRecibirTrama() -->
     * Descripción: Al Recibir Trama. Relaciona anuncios del llavero con ubicación y preparadores
     * -------------------- */
    @Override public void alRecibirTrama(IBeaconParser.Resultado trama, String mac) {
        principal.post(() -> {
            if (!activo || llavero == null || trama == null || trama.nombreBle == null
                    || !llavero.equalsIgnoreCase(trama.nombreBle)) return;
            oyente.alRecibirAnuncio();
            EstadoLlavero.COMPARTIDO.actualizar(trama, SystemClock.elapsedRealtime());
            // Temperatura y bateria se muestran aun sin una posicion GPS valida.
            if (trama.esTemperatura() && trama.obtenerTemperaturaC() != null) {
                if (PreparadorMediciones.errorUbicacion(ultima) == null) publicarTemperatura(trama);
                else {
                    temperaturaPendiente = trama;
                    instanteTemperaturaPendiente = SystemClock.elapsedRealtime();
                    oyente.alCambiarEstado("Temperatura recibida. Esperando ubicación válida para guardarla y enviarla.");
                }
            }
            if (!trama.esOzono()) return;
            // Se recuerda la trama mientras llega la primera ubicación; no se considera enviada.
            if (ultima == null) {
                pendienteUbicacion = trama;
                instantePendienteMs = SystemClock.elapsedRealtime();
                return;
            }
            publicarSiValida(trama);
        });
    }


    /** --------------------
     * Diseño lógico: ubicacion: Posicion --> alRecibirUbicacion() -->
     * Descripción: Al Recibir Ubicacion. Relaciona anuncios del llavero con ubicación y preparadores
     * -------------------- */
    @Override public void alRecibirUbicacion(Location ubicacion) {
        principal.post(() -> {
            if (!activo) return;
            ultima = ubicacion;
            oyente.alRecibirUbicacion(ubicacion);
            if (temperaturaPendiente != null) {
                long edad = SystemClock.elapsedRealtime() - instanteTemperaturaPendiente;
                if (edad < 0 || edad > MAX_ESPERA_TRAMA_MS) temperaturaPendiente = null;
                else if (PreparadorMediciones.errorUbicacion(ubicacion) == null) {
                    IBeaconParser.Resultado pendiente = temperaturaPendiente;
                    temperaturaPendiente = null;
                    publicarTemperatura(pendiente);
                }
            }
            if (pendienteUbicacion != null) {
                IBeaconParser.Resultado trama = pendienteUbicacion;
                long edad = SystemClock.elapsedRealtime() - instantePendienteMs;
                pendienteUbicacion = null;
                if (edad >= 0 && edad <= MAX_ESPERA_TRAMA_MS) publicarSiValida(trama);
            }
        });
    }


    /** --------------------
     * Diseño lógico: trama: IBeaconParserResultado --> publicarTemperatura() -->
     * Descripción: Prepara una lectura solo cuando haya posición válida; evita reenviar repetidas
     * -------------------- */
    private void publicarTemperatura(IBeaconParser.Resultado trama) {
        MedicionTemperatura medicion = preparadorTemperaturas.preparar(llavero, trama, ultima);
        if (medicion != null) oyente.alPrepararTemperatura(medicion);
    }


    /** --------------------
     * Diseño lógico: trama: IBeaconParserResultado --> publicarSiValida() -->
     * Descripción: Publicar Si Valida. Relaciona anuncios del llavero con ubicación y preparadores
     * -------------------- */
    private void publicarSiValida(IBeaconParser.Resultado trama) {
        PreparadorMediciones.Resultado r = preparador.preparar(llavero, trama, ultima);
        if (r.medicion != null) oyente.alPrepararMedicion(r.medicion, trama.rssi);
        else if (r.mensaje != null && !r.mensaje.startsWith("Anuncio BLE repetido"))
            oyente.alDescartarMedicion(r.mensaje);
    }


    /** --------------------
     * Diseño lógico: mensaje: Text --> alOcurrirError() -->
     * Descripción: Al Ocurrir Error. Relaciona anuncios del llavero con ubicación y preparadores
     * -------------------- */
    @Override public void alOcurrirError(String mensaje) { informar(mensaje); }


    /** --------------------
     * Diseño lógico: mensaje: Text --> informar() -->
     * Descripción: Informar. Relaciona anuncios del llavero con ubicación y preparadores
     * -------------------- */
    private void informar(String mensaje) { principal.post(() -> oyente.alCambiarEstado(mensaje)); }
}
