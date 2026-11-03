/*
 * ============================================================
 * Nombre: BleScanner.java
 * Descripción: Escanea anuncios BLE y comunica las tramas iBeacon
 *              de SENDA decodificadas por IBeaconParser.
 * Copyright (c) 2026 - Equipo 2 - SENDA / OZORA
 * Fecha: 02/11/2026
 * Autor: ---
 * Aportación: escaneo BLE y entrega de medidas a una interfaz
 *             de recepción sin depender de ninguna Activity.
 * ============================================================
 */
package com.example.ozora.senda.bluetooth;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

/**
 * Escáner BLE aislado de la interfaz gráfica.
 * La Activity que lo utilice se encarga de solicitar los permisos en tiempo de ejecución.
 */
public final class BleScanner {

    /** Recibe las medidas identificadas como anuncios SENDA y posibles errores. */
    public interface Oyente {
        // Diseño lógico: (IBeaconParser.Resultado, String) -> alRecibirTrama() -> (void)
        // Descripción: Notifica una trama válida y la dirección del emisor.
        void alRecibirTrama(IBeaconParser.Resultado medida, String direccionMac);

        // Diseño lógico: (String) -> alOcurrirError() -> (void)
        // Descripción: Comunica un error al componente que inició el escaneo.
        void alOcurrirError(String mensaje);
    }

    private final Context contexto;
    private final Oyente oyente;
    private BluetoothLeScanner escaner;
    private boolean escaneando;

    private final ScanCallback callback = new ScanCallback() {
        @Override
        // Diseño lógico: (int, ScanResult) -> onScanResult() -> (void)
        // Descripción: Analiza los datos del fabricante de cada anuncio recibido.
        public void onScanResult(int callbackType, ScanResult resultado) {
            if (resultado == null || resultado.getScanRecord() == null) {
                return;
            }
            byte[] fabricante = resultado.getScanRecord()
                    .getManufacturerSpecificData(IBeaconParser.COMPANY_ID_APPLE);
            String nombre = resultado.getScanRecord().getDeviceName();
            IBeaconParser.Resultado medida = IBeaconParser.interpretar(
                    fabricante, nombre, resultado.getRssi());
            if (medida != null) {
                oyente.alRecibirTrama(medida, resultado.getDevice().getAddress());
            }
        }

        @Override
        // Diseño lógico: (int) -> onScanFailed() -> (void)
        // Descripción: Notifica fallos de Android al iniciar o mantener el escaneo.
        public void onScanFailed(int codigoError) {
            escaneando = false;
            oyente.alOcurrirError("Error BLE al escanear. Código: " + codigoError);
        }
    };

    // Diseño lógico: (Context, Oyente) -> BleScanner() -> (BleScanner)
    // Descripción: Prepara el escáner sin iniciarlo.
    public BleScanner(Context contexto, Oyente oyente) {
        if (contexto == null || oyente == null) {
            throw new IllegalArgumentException("El contexto y el oyente son obligatorios");
        }
        this.contexto = contexto.getApplicationContext();
        this.oyente = oyente;
    }

    /**
     * Diseño lógico: () -> iniciarEscaneo() -> (boolean)
     * Descripción: Inicia escaneo BLE de baja latencia. Devuelve false y
     *              notifica error si faltan permisos o Bluetooth está apagado.
     */
    public boolean iniciarEscaneo() {
        if (escaneando) {
            return true;
        }
        if (!tienePermisos()) {
            oyente.alOcurrirError("Faltan permisos Bluetooth/ubicación. Solicítalos desde la Activity.");
            return false;
        }
        BluetoothManager gestor = (BluetoothManager) contexto.getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter adaptador = gestor == null ? null : gestor.getAdapter();
        if (adaptador == null || !adaptador.isEnabled()) {
            oyente.alOcurrirError("Bluetooth no disponible o desactivado.");
            return false;
        }
        escaner = adaptador.getBluetoothLeScanner();
        if (escaner == null) {
            oyente.alOcurrirError("No se ha podido obtener el escáner BLE.");
            return false;
        }
        ScanSettings ajustes = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build();
        try {
            ScanFilter filtro = new ScanFilter.Builder().setManufacturerData(
                    IBeaconParser.COMPANY_ID_APPLE, new byte[]{0x02, 0x15}).build();
            escaner.startScan(java.util.Collections.singletonList(filtro), ajustes, callback);
            escaneando = true;
            return true;
        } catch (SecurityException ex) {
            oyente.alOcurrirError("Permiso BLE denegado: " + ex.getMessage());
            return false;
        } catch (IllegalStateException ex) {
            oyente.alOcurrirError("Bluetooth no disponible: " + ex.getMessage());
            return false;
        }
    }

    /**
     * Diseño lógico: () -> detenerEscaneo() -> (void)
     * Descripción: Detiene la exploración activa y libera la referencia al escáner.
     */
    public void detenerEscaneo() {
        if (!escaneando || escaner == null) {
            return;
        }
        try {
            escaner.stopScan(callback);
        } catch (SecurityException ex) {
            oyente.alOcurrirError("No se ha podido detener el escaneo: " + ex.getMessage());
        } catch (IllegalStateException ex) {
            oyente.alOcurrirError("Bluetooth se ha desactivado.");
        } finally {
            escaneando = false;
            escaner = null;
        }
    }

    /**
     * Diseño lógico: () -> estaEscaneando() -> (boolean)
     * Descripción: Indica si se ha solicitado un escaneo activo.
     */
    public boolean estaEscaneando() {
        return escaneando;
    }

    /**
     * Diseño lógico: () -> tienePermisos() -> (boolean)
     * Descripción: Comprueba los permisos BLE/ubicación vigentes según la API.
     *              No solicita permisos; esa responsabilidad es de la Activity.
     */
    private boolean tienePermisos() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return contexto.checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN)
                    == PackageManager.PERMISSION_GRANTED
                    && contexto.checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT)
                    == PackageManager.PERMISSION_GRANTED
                    && contexto.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED;
        }
        return contexto.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }
}
